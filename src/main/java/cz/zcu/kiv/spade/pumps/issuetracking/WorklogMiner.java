package cz.zcu.kiv.spade.pumps.issuetracking;

import cz.zcu.kiv.spade.domain.FieldChange;
import cz.zcu.kiv.spade.domain.WorkItemChange;
import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.pumps.DataMiner;
import cz.zcu.kiv.spade.pumps.DataPump;

public abstract class WorklogMiner<WorklogObject> extends DataMiner {

    private static final String LOGTIME_FIELD_NAME = "spentTime";

    protected WorklogMiner(DataPump pump) {
        super(pump);
    }

    /**
     * mines a log time entry
     *
     * @param unit            issue
     * @param spentTimeBefore time spent on the issue previous to this entry
     * @param entry           log time entry
     */
    protected abstract void generateLogTimeConfiguration(WorkUnit unit, double spentTimeBefore, WorklogObject entry);

    protected WorkItemChange generateLogTimeChange(WorkUnit unit, double spentTimeBefore, double spentTime) {
        WorkItemChange change = new WorkItemChange();
        change.setChangedItem(unit);
        change.setType(WorkItemChange.Type.LOGTIME);

        FieldChange fieldChange = new FieldChange();
        fieldChange.setName(LOGTIME_FIELD_NAME);
        fieldChange.setOldValue(Double.toString(spentTimeBefore));
        fieldChange.setNewValue(Double.toString(spentTimeBefore + spentTime));

        change.getFieldChanges().add(fieldChange);
        return change;
    }
}
