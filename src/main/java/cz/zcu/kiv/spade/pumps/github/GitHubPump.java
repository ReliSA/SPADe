package cz.zcu.kiv.spade.pumps.github;

import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.EnumClass;
import cz.zcu.kiv.spade.domain.enums.EnumField;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.abstracts.ComplexPump;
import cz.zcu.kiv.spade.pumps.git.GitPump;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.*;

public class GitHubPump extends ComplexPump<GHRepository> {

    /**
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public GitHubPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
    }

    @Override
    protected GHRepository init() {
        GHRepository repo = null;
        try {
            GitHub gitHub = GitHub.connectUsingPassword(username, password);
            repo = gitHub.getRepository(getProjectFullName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return repo;
    }

    @Override
    public ProjectInstance mineData() {

        DataPump gitPump = new GitPump(projectHandle, null, null, null);
        ProjectInstance pi = gitPump.mineData();

        gitPump.close();

        pi.getToolInstance().setTool(Tool.GITHUB);
        pi.getProject().setDescription(rootObject.getDescription());

        Map<Integer, WorkUnit> unitMap = mineTickets();
        for (WorkUnit unit : unitMap.values()) {
            unit = resolveLabels(unit, pi);
            unit.setProject(pi.getProject());
        }

        linkTicketsToCommits(pi, unitMap);

        pi.getProject().getConfigurations().addAll(mineChanges(unitMap));
        List<Configuration> list = sortConfigsByDate(pi.getProject().getConfigurations());

        pi.getProject().setConfigurations(list);

        return pi;
    }

    @Override
    public Map<String, Configuration> mineBranches() {
        return null;
    }

    @Override
    public void addTags(Map<String, Configuration> configurationMap) {
    }

    @Override
    public void close() {
        super.close();
    }

    public Map<Integer, WorkUnit> mineTickets() {
        Map<Integer, WorkUnit> unitMap = new HashMap<>();

        Set<GHIssue> issues = rootObject.listIssues(GHIssueState.ALL).asSet();
        for (GHIssue issue : issues) {
            WorkUnit unit = new WorkUnit();
            unit.setExternalId(issue.getId() + "");
            unit.setNumber(issue.getNumber());
            unit.setUrl(issue.getHtmlUrl().toString());
            unit.setName(issue.getTitle());
            unit.setDescription(issue.getBody());
            unit.setAuthor(generatePerson(issue.getUser()));
            unit.setAssignee(generatePerson(issue.getAssignee()));
            unit.setStatus(issue.getState().name());
            //TODO issue.getMilestone();
            try {
                unit.setCreated(issue.getCreatedAt());
                if (issue.getLabels() != null && !issue.getLabels().isEmpty()) {
                    unit.setCategory(mineLabels(issue.getLabels()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            unitMap.put(unit.getNumber(), unit);
        }

        return unitMap;
    }

    private Collection<Configuration> mineChanges(Map<Integer, WorkUnit> unitMap) {
        Collection<Configuration> configurations = new LinkedHashSet<>();

        Set<GHIssue> issues = rootObject.listIssues(GHIssueState.ALL).asSet();
        for (GHIssue issue : issues) {

            Configuration creation = generateCreationConfig(unitMap.get(issue.getNumber()));
            configurations.add(creation);

            try {
                for (GHIssueComment comment : issue.getComments()) {
                    Configuration commentConf = generateCommentConfig(unitMap.get(issue.getNumber()), comment);
                    unitMap.put(issue.getNumber(), (WorkUnit) commentConf.getChanges().get(0).getChangedItem());
                    configurations.add(commentConf);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (issue.getClosedBy() != null && issue.getClosedAt() != null) {
                Configuration closure = generateClosureConfig(unitMap.get(issue.getNumber()), issue.getClosedAt(), issue.getClosedBy());
                configurations.add(closure);
            }
        }

        return configurations;
    }

    private void linkTicketsToCommits(ProjectInstance pi, Map<Integer, WorkUnit> unitMap) {
        for (Configuration commit : pi.getProject().getConfigurations()) {
            Collection<WorkUnit> toBeReplaced = new LinkedHashSet<>();
            Collection<WorkUnit> toBeAdded = new LinkedHashSet<>();
            for (WorkUnit unit : commit.getWorkUnits()) {
                if (unitMap.containsKey(unit.getNumber())) {
                    toBeReplaced.add(unit);
                    toBeAdded.add(unitMap.get(unit.getNumber()));
                }
            }
            commit.getWorkUnits().removeAll(toBeReplaced);
            commit.getWorkUnits().addAll(toBeAdded);
        }
    }

    private Configuration generateCreationConfig(WorkUnit unit) {
        WorkItemChange change = new WorkItemChange();
        change.setChangedItem(unit);

        if (unit.getDescription() != null)
            change.getFieldChanges().add(generateFieldChange("body", null, unit.getDescription()));
        if (unit.getAssignee() != null)
            change.getFieldChanges().add(generateFieldChange("asignee", null, unit.getAssignee().getName()));
        change.getFieldChanges().add(generateFieldChange("status", null, GHIssueState.OPEN.name()));

        Configuration configuration = new Configuration();
        configuration.setAuthor(unit.getAuthor());
        configuration.setCreated(unit.getCreated());
        configuration.setCommitted(configuration.getCreated());
        configuration.getChanges().add(change);

        return configuration;
    }

    private Configuration generateCommentConfig(WorkUnit unit, GHIssueComment comment) {
        WorkItemChange change = new WorkItemChange();
        Configuration configuration = new Configuration();

        try {
            String amendment = "\n\nComment by: " + comment.getUser().getLogin() + " (" + comment.getCreatedAt() + "): " + comment.getBody();
            change.getFieldChanges().add(generateFieldChange("comment", unit.getDescription(), unit.getDescription() + amendment));
            unit.setDescription(unit.getDescription() + amendment);

            change.setChangedItem(unit);

            configuration.setAuthor(generatePerson(comment.getUser()));
            configuration.setCreated(comment.getCreatedAt());
            configuration.setCommitted(configuration.getCreated());
            configuration.getChanges().add(change);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return configuration;
    }

    private Configuration generateClosureConfig(WorkUnit unit, Date closedAt, GHUser closedBy) {
        WorkItemChange change = new WorkItemChange();
        change.setChangedItem(unit);

        change.getFieldChanges().add(generateFieldChange("status", GHIssueState.OPEN.name(), GHIssueState.CLOSED.name()));

        Configuration configuration = new Configuration();
        configuration.setAuthor(generatePerson(closedBy));
        configuration.setCreated(closedAt);
        configuration.setCommitted(configuration.getCreated());
        configuration.getChanges().add(change);

        return configuration;
    }

    private FieldChange generateFieldChange(String field, String oldValue, String newValue) {
        FieldChange fChange = new FieldChange();
        fChange.setName(field);
        fChange.setOldValue(oldValue);
        fChange.setNewValue(newValue);

        return fChange;
    }

    private Person generatePerson(GHUser user) {
        if (user == null) return null;

        Identity identity = new Identity();
        identity.setExternalId(user.getId() + "");
        identity.setName(user.getLogin());
        try {
            identity.setDescription(user.getName());
            identity.setEmail(user.getEmail());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Person person = new Person();
        person.getIdentities().add(identity);
        return person;
    }

    private String mineLabels(Collection<GHLabel> labels) {
        String labelString = "";
        if (!labels.isEmpty()) {
            for (GHLabel label : labels) {
                labelString += label.getName() + ";";
            }
            labelString = labelString.substring(0, labelString.length() - 1);
        }
        return labelString;
    }

    private WorkUnit resolveLabels(WorkUnit unit, ProjectInstance pi) {
        if (unit.getCategory() == null || unit.getCategory().isEmpty()) return unit;

        String[] labels;
        if (!unit.getCategory().contains(";")) labels = new String[]{unit.getCategory()};
        else labels = unit.getCategory().split(";");

        String newCategory = "";

        for (String label : labels) {

            String normLabel = "";
            for (int i = 0; i < label.length(); i++) {
                if (Character.isLetter(label.charAt(i))) {
                    normLabel += Character.toLowerCase(label.charAt(i));
                }
            }

            boolean found = false;

            for (EnumKeyword keyword : pi.getKeywords()) {
                if (normLabel.equals(keyword.getName())) {
                    if (keyword.getFieldsAndClasses().size() == 1) {
                        FieldAndClass fac = keyword.getFieldsAndClasses().get(0);
                        if (fac.getEnumField().equals(EnumField.TYPE)) {
                            unit.setType(label);
                            found = true;
                        } else if (fac.getEnumField().equals(EnumField.RESOLUTION)) {
                            unit.setResolution(label);
                            found = true;
                        } else if (fac.getEnumField().equals(EnumField.SEVERITY) && !fac.getEnumClass().equals(EnumClass.NORMAL)) {
                            unit.setSeverity(label);
                            found = true;
                        } else if (fac.getEnumField().equals(EnumField.PRIORITY) && !fac.getEnumClass().equals(EnumClass.NORMAL)) {
                            unit.setPriority(label);
                            found = true;
                        }
                    }
                    //TODO N:N keywords
                }
                if (found) break;
            }

            if (!found) {
                newCategory += label + ";";
                //TODO prompt new keyword assignment
            }
        }
        if (newCategory.isEmpty()) unit.setCategory(null);
        else unit.setCategory(newCategory.substring(0, newCategory.length() - 1));

        if (!newCategory.isEmpty()) System.out.println(newCategory);

        return unit;
    }

    /**
     * gets project full name (organisation/project)
     *
     * @return project name
     */
    private String getProjectFullName() {
        return getProjectDir().substring(getProjectDir().indexOf("/") + 1);
    }
}
