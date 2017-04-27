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
import java.util.*;

public class CocaexFilePrinter {

    public void print(ProjectInstance pi) throws JSONException {

        Collection<JSONObject> nodes = new ArrayList<>();
        Collection<JSONObject> edges = new ArrayList<>();

        int nodesId = 0;
        Map<NamedEntity, Integer> nodeMap = new HashMap<>();

        for (Person person : pi.getProject().getPeople()) {
            JSONObject personNode = new JSONObject();

            personNode.put("id", nodesId);
            personNode.put("name", person.getName());

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
                authorNode.put("from", nodeMap.get(item.getAuthor()));
                authorNode.put("to", nodeMap.get(item));
                edges.add(authorNode);
            }


            if (item instanceof WorkUnit) {
                WorkUnit unit = (WorkUnit) item;
                if (unit.getAssignee() != null) {
                    JSONObject assigneeNode = new JSONObject();
                    assigneeNode.put("id", edgesId++);
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
                    relNode.put("from", nodeMap.get(relation.getPerson()));
                    relNode.put("to", nodeMap.get(commit));
                    edges.add(relNode);
                }
            }

            for (WorkItemRelation relation : item.getRelatedItems()) {

                JSONObject relNode = new JSONObject();
                relNode.put("id", edgesId++);
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
                    relNode.put("from", nodeMap.get(configuration));
                    relNode.put("to", nodeMap.get(changedItem));
                    edges.add(relNode);

                    if (configuration.getAuthor() == null) continue;

                    JSONObject editorNode = new JSONObject();
                    editorNode.put("id", edgesId++);
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
                            new FileOutputStream("cocaex/" + pi.getName() + "-" + pi.getToolInstance().getTool().name() + ".js")
                            , StandardCharsets.UTF_8), true);
            pw.print(root.toString(1));
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
        unitNode.put("name", "#" + unit.getNumber() + " " + unit.getName());

        return unitNode;
    }

    private JSONObject generateArtifactNode(Artifact artifact) throws JSONException {
        JSONObject artifactNode = new JSONObject();
        artifactNode.put("name", artifact.getName());

        return artifactNode;
    }

    private JSONObject generateConfigurationNode(Configuration configuration) throws JSONException {
        JSONObject configurationNode = new JSONObject();
        configurationNode.put("name", configuration.getChanges().get(0).getName());

        return configurationNode;
    }

    private JSONObject generateCommitedConfigurationNode(CommittedConfiguration committed) throws JSONException {
        JSONObject committedNode = new JSONObject();
        committedNode.put("name", committed.getChanges().get(0).getName());

        return committedNode;
    }

    private JSONObject generateCommitNode(Commit commit) throws JSONException {
        JSONObject commitNode = new JSONObject();
        commitNode.put("name", commit.getIdentifier());

        return commitNode;
    }
}