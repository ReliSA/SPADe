package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.Worklog;
import cz.zcu.kiv.spade.domain.CommittedConfiguration;
import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.pumps.issuetracking.WorklogMiner;

class JiraWorklogMiner extends WorklogMiner<Worklog> {

    JiraWorklogMiner(JiraPump pump) {
        super(pump);
    }

    @Override
    protected void generateLogTimeConfiguration(WorkUnit unit, double spentTimeBefore, Worklog entry) {
        CommittedConfiguration configuration = new CommittedConfiguration();
        configuration.setExternalId(entry.getSelf().toString());
        configuration.setAuthor(addPerson(((JiraPeopleMiner) pump.getPeopleMiner()).generateIdentity(entry.getAuthor())));
        configuration.setDescription(entry.getComment());
        configuration.setCommitted(entry.getCreationDate().toDate());
        configuration.setCreated(entry.getStartDate().toDate());

        configuration.getChanges().add(generateLogTimeChange(unit, spentTimeBefore, minutesToHours(entry.getMinutesSpent())));

        pump.getPi().getProject().getConfigurations().add(configuration);
    }

    void mineTimeTracking(WorkUnit unit, Issue issue) {
        if (issue.getTimeTracking() != null) {
            if (issue.getTimeTracking().getOriginalEstimateMinutes() != null) {
                unit.setEstimatedTime(minutesToHours(issue.getTimeTracking().getOriginalEstimateMinutes()));
            }
            if (issue.getTimeTracking().getTimeSpentMinutes() != null) {
                unit.setSpentTime(minutesToHours(issue.getTimeTracking().getTimeSpentMinutes()));
            }
            if (unit.getEstimatedTime() > 0) {
                int percentage = (int) (unit.getEstimatedTime() / unit.getSpentTime()) * PERCENTAGE_MAX;
                unit.setProgress(Math.min(percentage, PERCENTAGE_MAX));
            }
        }
    }
}
