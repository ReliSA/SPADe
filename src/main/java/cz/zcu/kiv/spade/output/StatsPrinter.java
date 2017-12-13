package cz.zcu.kiv.spade.output;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

public class StatsPrinter {

    public void print(ProjectInstance pi) {

        Map<String, Integer> branches = new HashMap<>();
        Set<String> tags = new LinkedHashSet<>();
        String defaultBranch = "";
        for (Commit commit : pi.getProject().getCommits()) {
            for (Branch branch : commit.getBranches()) {
                if (branch.getIsMain()) {
                    defaultBranch = branch.getName();
                }
                if (branches.containsKey(branch.getName())) {
                    branches.put(branch.getName(), branches.get(branch.getName()) + 1);
                } else {
                    branches.put(branch.getName(), 1);
                }
            }
            for (VCSTag tag : commit.getTags()) {
                tags.add(tag.getName());
            }
        }
        int commitComments = 0;
        int issueComments = 0;
        for (Configuration configuration : pi.getProject().getConfigurations()) {
            if (configuration.getChanges().size() == 1) {
                for (WorkItemChange change : configuration.getChanges()) {
                    if (change.getChangedItem() instanceof Commit && change.getName().equals(WorkItemChange.Type.COMMENT.name())) {
                        commitComments++;
                    }
                    if (change.getChangedItem() instanceof WorkUnit && (change.getName().equals(WorkItemChange.Type.COMMENT.name()) || !configuration.getDescription().isEmpty())) {
                        issueComments++;
                    }
                }
            }
        }
        Set<String> iterations = new LinkedHashSet<>();
        for (WorkUnit unit : pi.getProject().getUnits()) {
            iterations.add(unit.getIteration().getName());
        }

        String output = "branches: " + branches.toString() + "\n" +
                "commits: " + pi.getProject().getCommits().size() + "\n" +
                "default branch: " + defaultBranch + "\n" +
                "default branch commits: " + branches.get(defaultBranch) + "\n" +
                "tags : " + tags.size() + "\n" +
                "commit comments: " + commitComments + "\n" +
                "categories: " + pi.getCategories().size() + "\n" +
                "segments: " + iterations.size() + "\n" +
                "issue comments: " + issueComments + "\n" +
                "issues: " + pi.getProject().getUnits().size() + "\n" +
                "start: " + App.TIMESTAMP.format(new Date(pi.getStats().getStart())) + "\n" +
                "mining repository took: " + App.TIMESTAMP.format(new Date(pi.getStats().getRepo() - pi.getStats().getStart() - 3600000)) + "\n" +
                "mining GitHub took: " + App.TIMESTAMP.format(new Date(pi.getStats().getMining() - pi.getStats().getRepo() - 3600000)) + "\n" +
                "mining took: " + App.TIMESTAMP.format(new Date(pi.getStats().getMining() - pi.getStats().getStart() - 3600000)) + "\n" +
                "printing took: " + App.TIMESTAMP.format(new Date(pi.getStats().getPrinting() - pi.getStats().getMining() - 3600000)) + "\n" +
                "loading took: " + App.TIMESTAMP.format(new Date(pi.getStats().getLoading() - pi.getStats().getPrinting() - 3600000)) + "\n" +
                "job took: " + App.TIMESTAMP.format(new Date(pi.getStats().getLoading() - pi.getStats().getStart() - 3600000));


        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream("output/stats/" + pi.getName().replace("/", "-") + ".txt"), "UTF-8");
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
