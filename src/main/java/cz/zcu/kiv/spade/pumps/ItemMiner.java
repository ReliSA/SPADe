package cz.zcu.kiv.spade.pumps;

public abstract class ItemMiner<WorkItemObject> extends DataMiner {

    protected ItemMiner(DataPump pump) {
        super(pump);
    }

    public abstract void mineItems();

    protected abstract void mineItem(WorkItemObject item);
}
