package cz.zcu.kiv.spade.pumps.issuetracking;

import cz.zcu.kiv.spade.pumps.DataMiner;
import cz.zcu.kiv.spade.pumps.DataPump;

public abstract class WikiMiner extends DataMiner {

    protected static final String DESC_FIELD_NAME = "description";

    protected WikiMiner(DataPump pump) {
        super(pump);
    }

    /**
     * mines projects wiki
     */
    public abstract void mineWiki();
}
