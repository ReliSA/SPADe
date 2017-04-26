package cz.zcu.kiv.spade.output;

import cz.zcu.kiv.spade.domain.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimelineFilePrinter {

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public void print(ProjectInstance pi) throws JSONException {

        Collection<JSONObject> nodes = new ArrayList<>();
        Collection<JSONObject> edges = new ArrayList<>();


        int nodesId = 0;
        Map<WorkItem, Integer> itemMap = new HashMap<>();

        for (WorkItem item : pi.getProject().getAllItems()) {

            if (item.getCreated() == null) continue;
            JSONObject itemNode = new JSONObject();

            if (item instanceof WorkUnit) {
                itemNode = generateWorkUnitNode((WorkUnit) item);
            } else if (item instanceof Artifact) {
                itemNode = generateArtifactNode((Artifact) item);
            } else if (item instanceof Commit) {
                itemNode = generateCommitNode((Commit) item);
            } else if (item instanceof CommittedConfiguration) {
                itemNode = generateCommitedConfigurationNode((CommittedConfiguration) item);
            } else if (item instanceof Configuration) {
                itemNode = generateConfigurationNode((Configuration) item);
            }

            itemNode.put("id", nodesId);

            nodes.add(itemNode);
            itemMap.put(item, nodesId++);
        }

        int edgesId = 1;

        for (WorkItem item : pi.getProject().getAllItems()) {
            for (WorkItemRelation relation : item.getRelatedItems()) {
                JSONObject relNode = new JSONObject();
                relNode.put("id", edgesId++);
                relNode.put("stereotype", relation.getRelation().getClassification().getaClass().name());
                if (relation.getRelation() != null) relNode.put("name", relation.getRelation().getName());
                else relNode.put("name", relation.getRelation().getName());
                relNode.put("from", itemMap.get(item));
                relNode.put("to", itemMap.get(relation.getRelatedItem()));
                edges.add(relNode);
            }
            if (item instanceof Configuration) {
                Configuration configuration = (Configuration) item;
                for (WorkItemChange change : configuration.getChanges()) {
                    WorkItem changedItem = change.getChangedItem();
                    JSONObject relNode = new JSONObject();
                    relNode.put("id", edgesId++);
                    relNode.put("stereotype", "CHANGES");
                    relNode.put("name", "changes");
                    relNode.put("from", itemMap.get(configuration));
                    relNode.put("to", itemMap.get(changedItem));
                    edges.add(relNode);
                }
            }
        }

        JSONObject root = new JSONObject();
        root.put("nodes", nodes);
        root.put("edges", edges);

        PrintWriter pw = null;
        try {
             pw = new PrintWriter(
                                new OutputStreamWriter(
                                new FileOutputStream("Timeline/software/data/" + pi.getName() + "-" + pi.getToolInstance().getTool().name() + ".js")
                                , StandardCharsets.UTF_8), true);
            pw.print("define([],function(){return");
            pw.print(root.toString(1));
            pw.print(";});");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    private JSONObject generateWorkUnitNode(WorkUnit unit) throws JSONException {
        JSONObject unitNode = new JSONObject();

        String description = "Type: " + unit.getType().getName();
        if (unit.getAssignee() != null) description = description + "&lt;br /&gt;Assignee: " + unit.getAssignee().getName();
        else description = description + "&lt;br /&gt;Assignee: ";
        description = description + "&lt;br /&gt;Estimate: " + Math.round(100.0 * unit.getEstimatedTime()) / 100.0 + " hours";
        description = description + "&lt;br /&gt;Spent time: " + Math.round(100.0 * unit.getSpentTime()) / 100.0 + " hours";
        description = description + "&lt;br /&gt;Status: " + unit.getStatus().getName();
        description = description + "&lt;br /&gt;Progress: " + unit.getProgress() + "%";
        description = description + "&lt;br /&gt;Priority: " + unit.getPriority().getName();
        description = description + "&lt;br /&gt;Severity: " + unit.getSeverity().getName();
        if (unit.getResolution() != null) description = description + "&lt;br /&gt;Resolution: " + unit.getResolution().getName();
        else description = description + "&lt;br /&gt;Resolution: ";
        description = description + "&lt;br /&gt;Categories: " + unit.getCategories().toString();
        description = description + "&lt;br /&gt;Author: " + unit.getAuthor().getName();
        if (unit.getIteration() != null) description = description + "&lt;br /&gt;Iteration: " + unit.getIteration().getName();
        else description = description + "&lt;br /&gt;Iteration: ";
        description = description + "&lt;br /&gt;&lt;br /&gt;Description: " + unit.getDescription();
        description = description + "&lt;br /&gt;URL: " + unit.getUrl();

        unitNode.put("description", description);
        if (unit.getCreated() != null)
            unitNode.put("begin", format.format(unit.getCreated()));
        unitNode.put("stereotype", "person");
        unitNode.put("name", "#" + unit.getNumber() + " " + unit.getName());
        if (unit.getDueDate() != null)
            unitNode.put("end", format.format(unit.getDueDate()));

        JSONObject properties = new JSONObject();
        properties.put("startPrecision", "day");
        properties.put("endPrecision", "day");
        unitNode.put("properties", properties);

        return unitNode;
    }

    private JSONObject generateArtifactNode(Artifact artifact) throws JSONException {
        JSONObject artifactNode = new JSONObject();

        String description = "";
        description = description + "&lt;br /&gt;Type: " + artifact.getArtifactClass().name();
        description = description + "&lt;br /&gt;Mime type: " + artifact.getMimeType();
        if (artifact.getAuthor() != null) description = description + "&lt;br /&gt;Author: " + artifact.getAuthor().getName();
        else description = description + "&lt;br /&gt;Author: ";
        description = description + "&lt;br /&gt;URL: " + artifact.getUrl();

        artifactNode.put("description", description);
        artifactNode.put("begin", format.format(artifact.getCreated()));
        artifactNode.put("stereotype", "theory");
        artifactNode.put("name", artifact.getName());

        JSONObject properties = new JSONObject();
        properties.put("startPrecision", "day");
        properties.put("endPrecision", "day");
        artifactNode.put("properties", properties);

        return artifactNode;
    }

    private JSONObject generateConfigurationNode(Configuration configuration) throws JSONException {
        JSONObject configurationNode = new JSONObject();

        String description = "";
        description = description + "Author: " + configuration.getAuthor().getName();
        description = description + "&lt;br /&gt;URL: " + configuration.getUrl();
        for (WorkItemChange change : configuration.getChanges()) {
            for (FieldChange fieldChange : change.getFieldChanges()) {
                description = description + "&lt;br /&gt;- changed " + fieldChange.getName() + " from " + fieldChange.getOldValue() + " to " + fieldChange.getNewValue();
            }
        }
        description = description + "&lt;br /&gt;&lt;br /&gt;Comment: " + configuration.getDescription();

        configurationNode.put("description", description);
        if (configuration.getCreated() != null)
            configurationNode.put("begin", format.format(configuration.getCreated()));
        configurationNode.put("stereotype", "device");
        configurationNode.put("name", configuration.getChanges().get(0).getName());

        JSONObject properties = new JSONObject();
        properties.put("startPrecision", "day");
        properties.put("endPrecision", "day");
        configurationNode.put("properties", properties);

        return configurationNode;
    }

    private JSONObject generateCommitedConfigurationNode(CommittedConfiguration committed) throws JSONException {
        JSONObject committedNode = new JSONObject();

        String description = "";
        description = description + "Author: " + committed.getAuthor().getName();
        description = description + "&lt;br /&gt;URL: " + committed.getUrl();
        for (WorkItemChange change : committed.getChanges()) {
            for (FieldChange fieldChange : change.getFieldChanges()) {
                description = description + "&lt;br /&gt;- changed " + fieldChange.getName() + " from " + fieldChange.getOldValue() + " to " + fieldChange.getNewValue();
            }
        }
        description = description + "&lt;br /&gt;&lt;br /&gt;Comment: " + committed.getDescription();

        committedNode.put("description", description);
        committedNode.put("begin", format.format(committed.getCreated()));
        committedNode.put("stereotype", "device");
        committedNode.put("name", committed.getChanges().get(0).getName());
        committedNode.put("end", format.format(committed.getCommitted()));

        JSONObject properties = new JSONObject();
        properties.put("startPrecision", "day");
        properties.put("endPrecision", "day");
        committedNode.put("properties", properties);

        return committedNode;
    }

    private JSONObject generateCommitNode(Commit commit) throws JSONException {
        JSONObject commitNode = new JSONObject();

        String description = "";
        description = description + "Author: " + commit.getAuthor().getName();
        description = description + "&lt;br /&gt;URL: " + commit.getUrl();
        description = description + "&lt;br /&gt;Branches: ";
        for (Branch branch : commit.getBranches()) {
            description = description + branch.getName() + ", ";
        }
        description = description + "&lt;br /&gt;Tags: ";
        for (VCSTag tag : commit.getTags()) {
            description = description + tag.getName() + ", ";
        }
        for (WorkItemChange change : commit.getChanges()) {
            description = description + "&lt;br /&gt;File: " + change.getChangedItem().getName();
            description = description + "&lt;br /&gt;--" + change.getDescription();
            for (FieldChange fieldChange : change.getFieldChanges()) {
                description = description + "&lt;br /&gt;--- changed " + fieldChange.getName() + " from " + fieldChange.getOldValue() + " to " + fieldChange.getNewValue();
            }
        }

        commitNode.put("description", commit.getDescription());
        commitNode.put("begin", format.format(commit.getCreated()));
        commitNode.put("stereotype", "device");
        commitNode.put("name", "#" + commit.getIdentifier());
        commitNode.put("end", format.format(commit.getCommitted()));

        JSONObject properties = new JSONObject();
        properties.put("startPrecision", "day");
        properties.put("endPrecision", "day");
        commitNode.put("properties", properties);

        return commitNode;
    }
}