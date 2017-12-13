package cz.zcu.kiv.spade.pumps.issuetracking.assembla;

import com.assembla.Space;
import com.assembla.SpaceTool;
import com.assembla.StandupReport;
import com.assembla.User;
import com.assembla.client.AssemblaAPI;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueTrackingPump;
import cz.zcu.kiv.spade.pumps.vcs.git.GitPump;
import cz.zcu.kiv.spade.pumps.issuetracking.github.GitHubPump;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class AssemblaPump extends IssueTrackingPump<AssemblaAPI, Space> {

    private static final int GIT_TOOL_ID = 128;
    private static final int GITHUB_TOOL_ID = 22;
    private static final String STANDUP_CHANGE_DESC = "stand-up added";
    private static final String DESC_FORMAT = "Roadblocks:\n%s\n\nWhat I did:\n%s\n\nWhat I will do:\n%s";
    private static final String AWAY_DESC_FORMAT = "%s\n\nfrom: %s to: %s";

    /**
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public AssemblaPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
        this.tool = Tool.ASSEMBLA;
        enumsMiner = new AssemblaEnumsMiner(this);
        peopleMiner = new AssemblaPeopleMiner(this);
        issueMiner = new AssemblaIssueMiner(this);
        segmentMiner = new AssemblaSegmentMiner(this);
        wikiMiner = new AssemblaWikiMiner(this);
        relationMiner = new AssemblaRelationMiner(this);
    }

    @Override
    public ProjectInstance mineData(EntityManager em) {
        pi = super.mineData(em);

        mineGit(em);

        this.tool = Tool.ASSEMBLA;
        pi.getToolInstance().setTool(tool);
        setToolInstance();

        rootObject = init();

        User user = rootObject.users(username).get();
        secondaryObject = rootObject.spaces(user.getId()).get(projectHandle);

        Project project = new Project();
        project.setName(secondaryObject.getName());
        project.setDescription(secondaryObject.getDescription());
        project.setStartDate(convertDate(secondaryObject.getCreatedAt()));

        pi.setExternalId(secondaryObject.getId());
        pi.setProject(project);

        mineContent();
        mineStandUps();
        ((AssemblaPeopleMiner) peopleMiner).mineEmails();

        return pi;
    }

    private void mineStandUps() {
        LocalDate projectStart = convertDate(pi.getProject().getStartDate());
        LocalDate now = convertDate(new Date(System.currentTimeMillis()));
        for (StandupReport report : rootObject.standUps(pi.getExternalId()).getAll(projectStart, now)) {
            if (report.getSpaceId().equals(pi.getExternalId())) {
                Artifact artifact = new Artifact();
                artifact.setExternalId(report.getId());
                artifact.setCreated(convertDate(report.getCreatedAt()));
                artifact.setAuthor(peopleMiner.addPerson(((AssemblaPeopleMiner) peopleMiner).generateIdentity(report.getUserId())));
                artifact.setDescription(String.format(DESC_FORMAT, report.getRoadblocks(), report.getWhatIDid(), report.getWhatIWillDo()));

                if (report.getAwayFlag()) {
                    String newDescription = String.format(AWAY_DESC_FORMAT, artifact.getDescription(), report.getFrom().toString(), report.getTo().toString());
                    artifact.setDescription(newDescription);
                }

                WorkItemChange change = new WorkItemChange();
                change.setType(WorkItemChange.Type.ADD);
                change.setDescription(STANDUP_CHANGE_DESC);
                change.setChangedItem(artifact);

                CommittedConfiguration configuration = new CommittedConfiguration();
                configuration.setAuthor(artifact.getAuthor());
                configuration.setCreated(artifact.getCreated());
                configuration.setCommitted(convertDate(report.getFilledFor()));
                configuration.getChanges().add(change);
            }
        }
    }

    @Override
    protected AssemblaAPI init() {
        return (AssemblaAPI) AssemblaAPI.create(password, privateKeyLoc);
    }

    private void mineGit(EntityManager em) {
        for (SpaceTool repository : rootObject.tools(pi.getExternalId()).getRepositories()) {
            if (repository.getToolId() == GITHUB_TOOL_ID) {
                DataPump gitPump = new GitHubPump(repository.getUrl(), null, null, null);
                pi = gitPump.mineData(em);
                gitPump.close();
            }
            if (repository.getToolId() == GIT_TOOL_ID) {
                DataPump gitPump = new GitPump(repository.getUrl(), null, null, null);
                pi = gitPump.mineData(em);
                gitPump.close();
            }
            pi.getStats().setRepo(System.currentTimeMillis());
        }
    }

    Date convertDate(ZonedDateTime date) {
        return Date.from(date.toInstant());
    }

    Date convertDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    LocalDate convertDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
