package cz.zcu.kiv.spade.pumps.issuetracking.redmine;

import com.taskadapter.redmineapi.bean.TimeEntry;
import cz.zcu.kiv.spade.domain.CommittedConfiguration;
import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.pumps.issuetracking.WorklogMiner;

class RedmineWorklogMiner extends WorklogMiner<TimeEntry> {

    private static final String DESC_WITH_ACTIVITY_FORMAT = "%s\n\nActivity: %s";

    RedmineWorklogMiner(RedminePump pump) {
        super(pump);
    }

    @Override
    protected void generateLogTimeConfiguration(WorkUnit unit, double spentTimeBefore, TimeEntry entry) {
        CommittedConfiguration configuration = new CommittedConfiguration();
        configuration.setExternalId(entry.getId().toString());
        configuration.setAuthor(addPerson(((RedminePeopleMiner) pump.getPeopleMiner()).generateIdentity(entry.getUserId(), entry.getUserName())));
        configuration.setDescription(String.format(DESC_WITH_ACTIVITY_FORMAT, entry.getComment(), entry.getActivityName()));
        configuration.setCommitted(entry.getCreatedOn());
        configuration.setCreated(entry.getSpentOn());

        configuration.getChanges().add(generateLogTimeChange(unit, spentTimeBefore, entry.getHours()));

        pump.getPi().getProject().getConfigurations().add(configuration);
    }
}
