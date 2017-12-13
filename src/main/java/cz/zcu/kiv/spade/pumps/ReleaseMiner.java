package cz.zcu.kiv.spade.pumps;

public abstract class ReleaseMiner extends DataMiner {

    protected ReleaseMiner(DataPump pump) {
        super(pump);
    }

    /**
     * loads a map using commit's external ID as a key and a set of associated tags as a value
     */
    public abstract void mineTags();
}
