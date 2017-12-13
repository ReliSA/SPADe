package cz.zcu.kiv.spade.pumps.issuetracking;

import cz.zcu.kiv.spade.domain.FieldChange;
import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.pumps.DataMiner;
import cz.zcu.kiv.spade.pumps.DataPump;

import java.util.Collection;

public abstract class ChangelogMiner<ChangelogObject> extends DataMiner {

    protected ChangelogMiner(DataPump pump) {
        super(pump);
    }

    protected abstract void generateModification(WorkUnit unit, ChangelogObject changelog);

    /**
     * mines issue field changes from a history records
     *
     * @param changelog history records
     * @return list of field changes
     */
    protected abstract Collection<FieldChange> mineChanges(ChangelogObject changelog);
}
