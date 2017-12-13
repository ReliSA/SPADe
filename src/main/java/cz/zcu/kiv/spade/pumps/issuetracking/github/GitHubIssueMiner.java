package cz.zcu.kiv.spade.pumps.issuetracking.github;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueMiner;
import org.kohsuke.github.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

class GitHubIssueMiner extends IssueMiner<GHIssue> {

    private static final String ISSUES_LISTED_LOG_MSG = "issues listed";
    private static final String ISSUES_MINED_FORMAT = "mined %d tickets";
    private static final int ISSUES_BATCH_SIZE = 100;

    private final GitHubCommentMiner commentMiner;

    GitHubIssueMiner(GitHubPump pump) {
        super(pump);
        commentMiner = new GitHubCommentMiner(pump);
    }

    @Override
    public void mineItems() {
        Set<GHIssue> issues = ((GHRepository) pump.getRootObject()).listIssues(GHIssueState.ALL).asSet();
        App.printLogMsg(ISSUES_LISTED_LOG_MSG);

        int count = 1;
        for (int id = issues.size(); id > 0;) {

            if (((issues.size() - (id - 1)) % ISSUES_BATCH_SIZE) == 0) {
                ((GitHubPump) pump).checkRateLimit();
            }

            GHIssue issue;
            try {
                issue = ((GHRepository) pump.getRootObject()).getIssue(id);
            } catch (IOException e) {
                if (e instanceof FileNotFoundException) {
                    generateDeletedIssue(id);
                    id--;
                    continue;
                } else {
                    ((GitHubPump) pump).resetRootObject();
                    continue;
                }
            }

            if (!issue.isPullRequest()) {
                mineItem(issue);
                if ((count % ISSUES_BATCH_SIZE) == 0) {
                    App.printLogMsg(String.format(ISSUES_MINED_FORMAT, count));
                }
                count++;
            }
            id--;
        }
        App.printLogMsg(String.format(ISSUES_MINED_FORMAT, count - 1));
    }

    @Override
    protected void mineItem(GHIssue issue) {
        WorkUnit unit = new WorkUnit();
        unit.setNumber(issue.getNumber());
        unit.setExternalId(Long.toString(issue.getId()));
        unit.setUrl(issue.getHtmlUrl().toString());
        unit.setName(issue.getTitle());
        if (issue.getBody() != null) {
            unit.setDescription(issue.getBody().trim());
        }

        unit.setStatus(resolveStatus(issue.getState().name()));

        if (issue.getMilestone() != null) {
            Iteration iteration = new Iteration();
            iteration.setExternalId(Long.toString(issue.getMilestone().getId()));
            unit.setIteration(iteration);
        }

        Date creation;
        GHUser author, assignee;
        while (true) {
            try {
                author = issue.getUser();
                assignee = issue.getAssignee();
                creation = issue.getCreatedAt();
                break;
            } catch (IOException e) {
                ((GitHubPump) pump).resetRootObject();
            }
        }

        unit.setAuthor(addPerson(((GitHubPeopleMiner) pump.getPeopleMiner()).generateIdentity(author)));
        if (assignee != null)
            unit.setAssignee(addPerson(((GitHubPeopleMiner) pump.getPeopleMiner()).generateIdentity(assignee)));
        unit.setCreated(creation);
        unit.setStartDate(creation);
        resolveCategories(unit, issue);

        pump.getPi().getProject().addUnit(unit);

        mineHistory(unit, issue);
    }

    @Override
    protected void resolveCategories(WorkUnit unit, GHIssue issue) {
        Collection<GHLabel> labels;
        while (true) {
            try {
                labels = issue.getLabels();
                break;
            } catch (IOException e) {
                ((GitHubPump) pump).resetRootObject();
            }
        }
        if (labels.isEmpty()) return;

        for (GHLabel label : labels) {

            WorkUnitType type = resolveType(label.getName());
            if (type != null) {
                unit.setType(type);
                return;
            }

            Resolution resolution = resolveResolution(label.getName());
            if (resolution != null) {
                unit.setResolution(resolution);
                return;
            }

            Severity severity = resolveSeverity(label.getName());
            if (severity != null) {
                unit.setSeverity(severity);
                return;
            }

            Priority priority = resolvePriority(label.getName());
            if (priority != null) {
                unit.setPriority(priority);
                return;
            }

            boolean found = false;
            for (Category category : pump.getPi().getCategories()) {
                if (category.getName().equals(label.getName())) {
                    unit.getCategories().add(category);
                    found = true;
                    break;
                }
            }
            if (!found) {
                Category newCategory = new Category();
                newCategory.setExternalId(label.getUrl());
                newCategory.setName(label.getName());
                newCategory.setDescription(label.getColor());
                unit.getCategories().add(newCategory);
                pump.getPi().getCategories().add(newCategory);
            }
        }
    }

    @Override
    protected Severity resolveSeverity(GHIssue issue) {
        // GH pump uses only resolveSeverity wo. return type
        return null;
    }

    @Override
    protected void mineWorklogs(WorkUnit unit, GHIssue issue) {
        // GitHub doesn't track spent time
    }

    @Override
    protected void mineComments(WorkUnit unit, GHIssue issue) {
        Collection<GHIssueComment> comments;
        while (true) {
            try {
                comments = issue.getComments();
                break;
            } catch (IOException e) {
                ((GitHubPump) pump).resetRootObject();
            }
        }

        for (GHIssueComment comment : comments) {
            commentMiner.generateUnitCommentConfig(unit, comment);
        }
    }

    @Override
    protected void mineHistory(WorkUnit unit, GHIssue issue) {
        super.mineHistory(unit, issue);

        GHUser closer;
        while (true) {
            try {
                closer = issue.getClosedBy();
                break;
            } catch (IOException e) {
                ((GitHubPump) pump).resetRootObject();
            }
        }

        if (closer != null && issue.getClosedAt() != null) {
            generateClosureConfig(unit, issue.getClosedAt(), closer);
        } else if (issue.getClosedAt() != null) {
            generateClosureConfig(unit, issue.getClosedAt());
        } else if (closer != null) {
            generateClosureConfig(unit, closer);
        }
    }

    /**
     * generates a Configuration representing the closure of a Work Unit (issue)
     *
     * @param unit     Work Unit to link to the closure Configuration
     * @param closedOn date of closure
     * @param closedBy a GitHub User who closed the issue
     */
    private void generateClosureConfig(WorkUnit unit, Date closedOn, GHUser closedBy) {
        Configuration closure = generateClosureConfig(unit, closedOn);
        closure.setAuthor(addPerson(((GitHubPeopleMiner) pump.getPeopleMiner()).generateIdentity(closedBy)));
    }

    private void generateClosureConfig(WorkUnit unit, GHUser closedBy) {
        Configuration closure = generateClosureConfig(unit, (Date) null);
        closure.setAuthor(addPerson(((GitHubPeopleMiner) pump.getPeopleMiner()).generateIdentity(closedBy)));
    }
}
