package cz.zcu.kiv.spade.output;

public class StatsBean {
    private long start;
    private long repo;
    private long mining;
    private long printing;
    private long loading;

    public StatsBean() {
        start = repo = mining = printing = loading = 0;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getRepo() {
        return repo;
    }

    public void setRepo(long repo) {
        this.repo = repo;
    }

    public long getMining() {
        return mining;
    }

    public void setMining(long mining) {
        this.mining = mining;
    }

    long getPrinting() {
        return printing;
    }

    public void setPrinting(long printing) {
        this.printing = printing;
    }

    public long getLoading() {
        return loading;
    }

    public void setLoading(long loading) {
        this.loading = loading;
    }

}
