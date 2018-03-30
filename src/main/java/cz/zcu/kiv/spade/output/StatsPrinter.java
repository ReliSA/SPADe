package cz.zcu.kiv.spade.output;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.StatusClass;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

public class StatsPrinter {

    private ProjectInstance pi;
    private Map<String, Integer> branches;
    private Set<String> tags, iterations;
    private String defaultBranch;
    private int commitComments, issueComments, logtimeComments, pureComments, changeComments, deletedIssues;

    public StatsPrinter(ProjectInstance pi) {
        this.pi = pi;
        branches = new HashMap<>();
        tags = new LinkedHashSet<>();
        defaultBranch = "";
        commitComments = 0;
        issueComments = 0;
        logtimeComments = 0;
        pureComments = 0;
        changeComments = 0;
        iterations = new LinkedHashSet<>();
    }

    public void print() {
        getRepoStats();
        getCommentStats();
        getIssueStats();
        write(buildString().toString());
    }

    private StringBuilder buildString() {
        StringBuilder output = new StringBuilder("branches: " + branches.size());
        for (Map.Entry<String, Integer> branch : branches.entrySet()) {
            output.append("\n\t").append(branch.getKey()).append(": ").append(branch.getValue());
        }
        output.append("\ncommits: ").append(pi.getProject().getCommits().size());
        output.append("\ndefault branch: ").append(defaultBranch);
        output.append("\ntags : ").append(tags.size());
        output.append("\ncommit comments: ").append(commitComments);
        output.append("\ncategories: ").append(pi.getCategories().size());
        output.append("\nsegments: ").append(iterations.size());
        output.append("\nissues: ").append(pi.getProject().getUnits().size());
        output.append("\n\texisting: ").append(pi.getProject().getUnits().size() - deletedIssues);
        output.append("\n\tdeleted: ").append(deletedIssues);
        output.append("\nissue comments: ").append(issueComments);
        output.append("\n\ton time logs: ").append(logtimeComments);
        output.append("\n\ton changes: ").append(changeComments);
        output.append("\n\tsolitary: ").append(pureComments);
        output.append("\nstart: ").append(App.TIMESTAMP.format(new Date(pi.getStats().getStart())));
        output.append("\njob took: ").append(App.TIMESTAMP.format(new Date(pi.getStats().getLoading() - pi.getStats().getStart() - 3600000)));
        output.append("\n\tmining: ").append(App.TIMESTAMP.format(new Date(pi.getStats().getMining() - pi.getStats().getStart() - 3600000)));
        output.append("\n\t\trepository: ").append(App.TIMESTAMP.format(new Date(pi.getStats().getRepo() - pi.getStats().getStart() - 3600000)));
        output.append("\n\t\tissue-tracker: ").append(App.TIMESTAMP.format(new Date(pi.getStats().getMining() - pi.getStats().getRepo() - 3600000)));
        output.append("\n\tprinting: ").append(App.TIMESTAMP.format(new Date(pi.getStats().getPrinting() - pi.getStats().getMining() - 3600000)));
        output.append("\n\tloading: ").append(App.TIMESTAMP.format(new Date(pi.getStats().getLoading() - pi.getStats().getPrinting() - 3600000)));
        return output;
    }

    private void getIssueStats() {
        for (WorkUnit unit : pi.getProject().getUnits()) {
            if (unit.getStatus().getaClass().equals(StatusClass.DELETED)) {
                deletedIssues += 1;
            }
            if (unit.getIteration() != null) {
                iterations.add(unit.getIteration().getName());
            }
        }
    }

    private void getCommentStats() {
        for (Configuration configuration : pi.getProject().getConfigurations()) {
            boolean issueComment = false;
            boolean spentTime = false;
            for (WorkItemChange change : configuration.getChanges()) {
                if (change.getType().equals(WorkItemChange.Type.COMMENT)) {
                    if (change.getChangedItem() instanceof Commit) {
                        commitComments++;
                    } else if (change.getChangedItem() instanceof WorkUnit) {
                        issueComments++;
                        issueComment = true;
                    }
                } else if (change.getType().equals(WorkItemChange.Type.LOGTIME)) {
                    spentTime = true;
                }
            }
            if (issueComment) {
                if (configuration.getChanges().size() == 1) {
                    pureComments++;
                } else if (configuration.getChanges().size() == 2 && spentTime) {
                    logtimeComments++;
                } else {
                    changeComments++;
                }
            }
        }
    }

    private void getRepoStats() {
        for (Commit commit : pi.getProject().getCommits()) {
            for (Branch branch : commit.getBranches()) {
                if (branch.getIsMain()) {
                    defaultBranch = branch.getName();
                }
                if (branches.containsKey(branch.getName())) {
                    branches.put(branch.getName(), branches.get(branch.getName()) + 1);
                } else {
                    branches.put(branch.getName(), 1);
                }
            }
            for (VCSTag tag : commit.getTags()) {
                tags.add(tag.getName());
            }
        }
    }

    private void write(String output) {
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream("output/stats/" + pi.getName().replace("/", "-") + ".txt"), "UTF-8");
            writer.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
