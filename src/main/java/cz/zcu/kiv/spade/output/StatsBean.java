package cz.zcu.kiv.spade.output;

import java.util.HashMap;
import java.util.Map;

public class StatsBean {

    private String name;
    private int commits;
    private Map<String, Integer> branchList;
    private String defaultBranch;
    private int defaultBranchCommits;
    private int tags;
    private int commitComments;
    private int labels;
    private int categories;
    private int milestones;
    private int issues;
    private int issueComments;
    private int realIssues;
    private int tags2;
    private int releases;
    private long start;
    private long repo;
    private long mining;
    private long printing;
    private long loading;

    public StatsBean() {
        branchList = new HashMap<>();
        name = defaultBranch = "";
        commits = defaultBranchCommits = tags = commitComments = labels = categories = milestones = issues
                = issueComments = realIssues = tags2 = releases = 0;
        start = repo = mining = printing = loading = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBranches() {
        return branchList.size();
    }

    public int getCommits() {
        return commits;
    }

    public void setCommits(int commits) {
        this.commits = commits;
    }

    String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
        this.defaultBranchCommits = branchList.get(defaultBranch);
    }

    int getDefaultBranchCommits() {
        return defaultBranchCommits;
    }

    public int getTags() {
        return tags;
    }

    public void setTags(int tags) {
        this.tags = tags;
    }

    int getCommitComments() {
        return commitComments;
    }

    public void setCommitComments(int commitComments) {
        this.commitComments = commitComments;
    }

    public int getLabels() {
        return labels;
    }

    public void setLabels(int labels) {
        this.labels = labels;
    }

    int getMilestones() {
        return milestones;
    }

    public void setMilestones(int milestones) {
        this.milestones = milestones;
    }

    public int getIssues() {
        return issues;
    }

    public void setIssues(int issues) {
        this.issues = issues;
    }

    int getRealIssues() {
        return realIssues;
    }

    public void setRealIssues(int realIssues) {
        this.realIssues = realIssues;
    }

    int getTags2() {
        return tags2;
    }

    public void setTags2(int tags2) {
        this.tags2 = tags2;
    }

    int getReleases() {
        return releases;
    }

    public void setReleases(int releases) {
        this.releases = releases;
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

    public Map<String, Integer> getBranchList() {
        return branchList;
    }

    public int getCategories() {
        return categories;
    }

    public void setCategories(int categories) {
        this.categories = categories;
    }

    public int getIssueComments() {
        return issueComments;
    }

    public void setIssueComments(int issueComments) {
        this.issueComments = issueComments;
    }
}
