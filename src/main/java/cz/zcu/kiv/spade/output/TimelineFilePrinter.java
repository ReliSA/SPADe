package cz.zcu.kiv.spade.output;

import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.abstracts.NamedEntity;
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
        Map<NamedEntity, Integer> nodeMap = new HashMap<>();

        for (Person person : pi.getProject().getPeople()) {
            JSONObject personNode = new JSONObject();

            personNode.put("id", nodesId);
            personNode.put("name", person.getName());
            personNode.put("stereotype", "person");

            Date startDate = new Date(Long.MAX_VALUE);
            Date endDate = new Date(Long.MIN_VALUE);

            for (WorkItem item : pi.getProject().getAllItems()) {
                if (item.getAuthor() != null && item.getAuthor().equals(person)) {
                    if (item.getCreated().before(startDate)) startDate = item.getCreated();
                    if (item.getCreated().after(endDate)) endDate = item.getCreated();
                }
                if (item instanceof Commit) {
                    Commit commit = (Commit) item;
                    for (ConfigPersonRelation relation : commit.getRelations()) {
                        if (relation.getPerson().equals(person)) {
                            if (item.getCreated().before(startDate)) startDate = item.getCreated();
                            if (item.getCreated().after(endDate)) endDate = item.getCreated();
                        }
                    }
                }
                if (item instanceof WorkUnit) {
                    WorkUnit unit = (WorkUnit) item;
                    if (unit.getAssignee() != null && unit.getAssignee().equals(person)) {
                        if (item.getCreated().before(startDate)) startDate = item.getCreated();
                        if (item.getCreated().after(endDate)) endDate = item.getCreated();

                        if (unit.getStartDate().before(startDate)) startDate = unit.getStartDate();
                        if (unit.getStartDate().after(endDate)) endDate = unit.getStartDate();

                        if (unit.getDueDate() != null) {
                            if (unit.getDueDate().before(startDate)) startDate = unit.getDueDate();
                            if (unit.getDueDate().after(endDate)) endDate = unit.getDueDate();
                        }
                    }
                }
            }

            if (startDate.getTime() < Long.MAX_VALUE) {
                personNode.put("begin", format.format(startDate));
            } else {
                personNode.put("begin", format.format(pi.getProject().getStartDate()));
            }

            if (endDate.getTime() > Long.MIN_VALUE) {
                personNode.put("end", format.format(endDate));
            } else {
                personNode.put("end", format.format(new Date()));

            }


            JSONObject properties = new JSONObject();
            properties.put("startPrecision", "day");
            properties.put("endPrecision", "day");
            personNode.put("properties", properties);

            nodes.add(personNode);
            nodeMap.put(person, nodesId++);
        }

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
            nodeMap.put(item, nodesId++);
        }

        int edgesId = 1;

        for (WorkItem item : pi.getProject().getAllItems()) {

            if (item.getAuthor() != null) {
                JSONObject authorNode = new JSONObject();
                authorNode.put("id", edgesId++);
                authorNode.put("stereotype", "AUTHOR");
                authorNode.put("name", "authors");
                authorNode.put("from", nodeMap.get(item.getAuthor()));
                authorNode.put("to", nodeMap.get(item));
                edges.add(authorNode);
            }


            if (item instanceof WorkUnit) {
                WorkUnit unit = (WorkUnit) item;
                if (unit.getAssignee() != null) {
                    JSONObject assigneeNode = new JSONObject();
                    assigneeNode.put("id", edgesId++);
                    assigneeNode.put("stereotyp", "ASSIGNEE");
                    assigneeNode.put("name", "responsible for");
                    assigneeNode.put("from", nodeMap.get(unit.getAssignee()));
                    assigneeNode.put("to", nodeMap.get(unit));
                    edges.add(assigneeNode);
                }
            }

            if (item instanceof Commit) {
                Commit commit = (Commit) item;
                for (ConfigPersonRelation relation : commit.getRelations()) {
                    JSONObject relNode = new JSONObject();
                    relNode.put("id", edgesId++);
                    relNode.put("stereotype", "FOOTLINE");
                    relNode.put("name", relation.getName());
                    relNode.put("from", nodeMap.get(relation.getPerson()));
                    relNode.put("to", nodeMap.get(commit));
                    edges.add(relNode);
                }
            }

            for (WorkItemRelation relation : item.getRelatedItems()) {

                if (!nodeMap.containsKey(item) || !nodeMap.containsKey(relation.getRelatedItem())) continue;
                JSONObject relNode = new JSONObject();
                relNode.put("id", edgesId++);
                relNode.put("stereotype", relation.getRelation().getClassification().getaClass().name());
                if (relation.getRelation() != null) relNode.put("name", relation.getRelation().getName());
                else relNode.put("name", relation.getRelation().getName());
                relNode.put("from", nodeMap.get(item));
                relNode.put("to", nodeMap.get(relation.getRelatedItem()));
                edges.add(relNode);
            }

            if (item instanceof Configuration) {
                Configuration configuration = (Configuration) item;
                for (WorkItemChange change : configuration.getChanges()) {
                    WorkItem changedItem = change.getChangedItem();
                    JSONObject relNode = new JSONObject();
                    relNode.put("id", edgesId++);
                    relNode.put("stereotype", change.getName());
                    relNode.put("name", change.getDescription());
                    relNode.put("from", nodeMap.get(configuration));
                    relNode.put("to", nodeMap.get(changedItem));
                    edges.add(relNode);

                    if (configuration.getAuthor() == null) continue;

                    JSONObject editorNode = new JSONObject();
                    editorNode.put("id", edgesId++);
                    editorNode.put("stereotype", change.getName());
                    editorNode.put("name", change.getDescription());
                    editorNode.put("from", nodeMap.get(configuration.getAuthor()));
                    editorNode.put("to", nodeMap.get(changedItem));
                    edges.add(editorNode);
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
        description = description + "\nEstimate: " + Math.round(100.0 * unit.getEstimatedTime()) / 100.0 + " hours";
        description = description + "\nSpent time: " + Math.round(100.0 * unit.getSpentTime()) / 100.0 + " hours";
        description = description + "\nStatus: " + unit.getStatus().getName();
        description = description + "\nProgress: " + unit.getProgress() + "%";
        description = description + "\nPriority: " + unit.getPriority().getName();
        description = description + "\nSeverity: " + unit.getSeverity().getName();
        description = description + "\nResolution: ";
        if (unit.getResolution() != null) description = description + unit.getResolution().getName();
        description = description + "\nCategories: " + unit.getCategories().toString();
        description = description + "\nIteration: ";
        if (unit.getIteration() != null) description = description + unit.getIteration().getName();
        description = description + "\n\nDescription: " + unit.getDescription();
        description = description + "\nURL: " + unit.getUrl();

        unitNode.put("description", description);
        if (unit.getCreated() != null)
            unitNode.put("begin", format.format(unit.getCreated()));
        unitNode.put("stereotype", "place");
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

        String description = "\nType: " + artifact.getArtifactClass().name();
        description = description + "\nMime type: " + artifact.getMimeType();
        description = description + "\nURL: " + artifact.getUrl();

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

        StringBuilder descriptionBuilder = new StringBuilder("\nURL: " + configuration.getUrl());
        for (WorkItemChange change : configuration.getChanges()) {
            for (FieldChange fieldChange : change.getFieldChanges()) {
                descriptionBuilder.append("\n- changed ").append(fieldChange.getName()).append(" from ").append(fieldChange.getOldValue()).append(" to ").append(fieldChange.getNewValue());
            }
        }
        descriptionBuilder.append("\n\nComment: ").append(configuration.getDescription());

        configurationNode.put("description", descriptionBuilder.toString());
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

        StringBuilder descriptionBuilder = new StringBuilder("\nURL: " + committed.getUrl());
        for (WorkItemChange change : committed.getChanges()) {
            for (FieldChange fieldChange : change.getFieldChanges()) {
                descriptionBuilder.append("\n- changed ").append(fieldChange.getName()).append(" from ").append(fieldChange.getOldValue()).append(" to ").append(fieldChange.getNewValue());
            }
        }
        descriptionBuilder.append("\n\nComment: ").append(committed.getDescription());

        committedNode.put("description", descriptionBuilder.toString());
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

        StringBuilder descriptionBuilder = new StringBuilder("\nURL: " + commit.getUrl());
        descriptionBuilder.append("\nBranches: ");
        for (Branch branch : commit.getBranches()) {
            descriptionBuilder.append(branch.getName()).append(", ");
        }

        descriptionBuilder.append("\nTags: ");
        for (VCSTag tag : commit.getTags()) {
            descriptionBuilder.append(tag.getName()).append(", ");
        }

        commitNode.put("description", descriptionBuilder.toString());
        commitNode.put("begin", format.format(commit.getCreated()));
        commitNode.put("stereotype", "device");
        commitNode.put("name", commit.getIdentifier());
        commitNode.put("end", format.format(commit.getCommitted()));

        JSONObject properties = new JSONObject();
        properties.put("startPrecision", "day");
        properties.put("endPrecision", "day");
        commitNode.put("properties", properties);

        return commitNode;
    }
}