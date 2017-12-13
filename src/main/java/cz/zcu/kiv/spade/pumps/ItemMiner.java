package cz.zcu.kiv.spade.pumps;

public abstract class ItemMiner<WorkItemObject> extends DataMiner {

    protected static final int PERCENTAGE_MAX = 100;

    protected ItemMiner(DataPump pump) {
        super(pump);
    }

    public abstract void mineItems();

    protected abstract void mineItem(WorkItemObject item);
}
