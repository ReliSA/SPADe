package cz.zcu.kiv.spade.output;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

public class StatsPrinter {

    public void print(ProjectInstance pi) {
        StatsBean bean = pi.getStats();

        String output = "branches: " + bean.getBranches() + "\n" +
                "commits: " + bean.getCommits() + "\n" +
                "default branch: " + bean.getDefaultBranch() + "\n" +
                "default branch commits: " + bean.getDefaultBranchCommits() + "\n" +
                "tags (in Git) : " + bean.getTags() + "\n" +
                "commit comments: " + bean.getCommitComments() + "\n" +
                "labels: " + bean.getLabels() + "\n" +
                "categories: " + bean.getCategories() + "\n" +
                "milestones: " + bean.getMilestones() + "\n" +
                "issues (with pull requests): " + bean.getIssues() + "\n" +
                "issue comments: " + bean.getIssueComments() + "\n" +
                "issues: " + bean.getRealIssues() + "\n" +
                "tags (in GitHub): " + bean.getTags2() + "\n" +
                "releases: " + bean.getReleases() + "\n" +
                "start: " + App.TIMESTAMP.format(new Date(bean.getStart())) + "\n" +
                "mining repository took: " + App.TIMESTAMP.format(new Date(bean.getRepo() - bean.getStart() - 3600000)) + "\n" +
                "mining GitHub took: " + App.TIMESTAMP.format(new Date(bean.getMining() - bean.getRepo() - 3600000)) + "\n" +
                "mining took: " + App.TIMESTAMP.format(new Date(bean.getMining() - bean.getStart() - 3600000)) + "\n" +
                "printing took: " + App.TIMESTAMP.format(new Date(bean.getPrinting() - bean.getMining() - 3600000)) + "\n" +
                "loading took: " + App.TIMESTAMP.format(new Date(bean.getLoading() - bean.getPrinting() - 3600000)) + "\n" +
                "job took: " + App.TIMESTAMP.format(new Date(bean.getLoading() - bean.getStart() - 3600000));


        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream("output/stats/" + bean.getName().replace("/", "-") + ".txt"), "UTF-8");
            writer.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
