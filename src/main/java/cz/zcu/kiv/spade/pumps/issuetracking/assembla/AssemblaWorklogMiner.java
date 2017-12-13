package cz.zcu.kiv.spade.pumps.issuetracking.assembla;

import com.assembla.Task;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.CommittedConfiguration;
import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.pumps.issuetracking.WorklogMiner;

class AssemblaWorklogMiner extends WorklogMiner<Task> {

    private static final String CONF_DESC_FORMAT = "%s\n\nBilled: %s\n\nEndAt: %s";

    AssemblaWorklogMiner(AssemblaPump pump) {
        super(pump);
    }

    @Override
    protected void generateLogTimeConfiguration(WorkUnit unit, double spentTimeBefore, Task entry) {
        CommittedConfiguration configuration = new CommittedConfiguration();
        configuration.setExternalId(entry.getId().toString());
        configuration.setAuthor(addPerson(((AssemblaPeopleMiner) pump.getPeopleMiner()).generateIdentity(entry.getUserId())));
        configuration.setDescription(String.format(CONF_DESC_FORMAT, entry.getDescription(), entry.getBilled(),entry.getEndAt()));
        configuration.setCommitted(((AssemblaPump) pump).convertDate(entry.getCreatedAt()));
        configuration.setCreated(((AssemblaPump) pump).convertDate(entry.getBeginAt()));
        App.printLogMsg(configuration.getDescription(), false);

        configuration.getChanges().add(generateLogTimeChange(unit, spentTimeBefore, entry.getHours()));

        pump.getPi().getProject().getConfigurations().add(configuration);
    }
}
