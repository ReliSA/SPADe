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
            personNode.put("name", nodesId);
            personNode.put("symbolicName", person.getName());

            personNode.put("exportedPackages", new ArrayList<String>());
            personNode.put("importedPackages", new ArrayList<String>());

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
                itemNode = generateCommittedConfigurationNode((CommittedConfiguration) item);
            } else if (item instanceof Configuration) {
                itemNode = generateConfigurationNode((Configuration) item);
            }

            itemNode.put("id", nodesId);
            itemNode.put("name", nodesId);

            itemNode.put("exportedPackages", new ArrayList<String>());
            itemNode.put("importedPackages", new ArrayList<String>());

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
                List<String> cons = new ArrayList<>();
                cons.add("AUTHORS");
                authorNode.put("packageConnections", cons);
                edges.add(authorNode);
            }


            if (item instanceof WorkUnit) {
                WorkUnit unit = (WorkUnit) item;
                if (unit.getAssignee() != null) {
                    JSONObject assigneeNode = new JSONObject();
                    assigneeNode.put("id", edgesId++);
                    assigneeNode.put("from", nodeMap.get(unit.getAssignee()));
                    assigneeNode.put("to", nodeMap.get(unit));
                    List<String> cons = new ArrayList<>();
                    cons.add("ASSIGNEE");
                    assigneeNode.put("packageConnections", cons);
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
                    List<String> cons = new ArrayList<>();
                    cons.add("FOOTLINE");
                    relNode.put("packageConnections", cons);
                    edges.add(relNode);
                }
            }

            for (WorkItemRelation relation : item.getRelatedItems()) {

                JSONObject relNode = new JSONObject();
                relNode.put("id", edgesId++);
                relNode.put("from", nodeMap.get(item));
                relNode.put("to", nodeMap.get(relation.getRelatedItem()));
                List<String> cons = new ArrayList<>();
                cons.add(relation.getRelation().getClassification().getAClass().name());
                relNode.put("packageConnections", cons);
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
                    List<String> cons = new ArrayList<>();
                    cons.add(change.getName());
                    relNode.put("packageConnections", cons);
                    edges.add(relNode);

                    if (configuration.getAuthor() == null) continue;

                    JSONObject editorNode = new JSONObject();
                    editorNode.put("id", edgesId++);
                    editorNode.put("from", nodeMap.get(configuration.getAuthor()));
                    editorNode.put("to", nodeMap.get(changedItem));
                    editorNode.put("packageConnections", cons);
                    edges.add(editorNode);
                }
            }
        }

        JSONObject root = new JSONObject();
        root.put("vertices", nodes);
        root.put("edges", edges);

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream("output/cocaex/" + pi.getName() + "-" + pi.getToolInstance().getTool().name() + ".json")
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
        unitNode.put("symbolicName", "#" + unit.getNumber() + " " + unit.getName());

        return unitNode;
    }

    private JSONObject generateArtifactNode(Artifact artifact) throws JSONException {
        JSONObject artifactNode = new JSONObject();
        artifactNode.put("symbolicName", artifact.getName());

        return artifactNode;
    }

    private JSONObject generateConfigurationNode(Configuration configuration) throws JSONException {
        JSONObject configurationNode = new JSONObject();
        configurationNode.put("symbolicName", configuration.getChanges().get(0).getName());

        return configurationNode;
    }

    private JSONObject generateCommittedConfigurationNode(CommittedConfiguration committed) throws JSONException {
        JSONObject committedNode = new JSONObject();
        committedNode.put("symbolicName", committed.getChanges().get(0).getName());

        return committedNode;
    }

    private JSONObject generateCommitNode(Commit commit) throws JSONException {
        JSONObject commitNode = new JSONObject();
        commitNode.put("symbolicName", commit.getIdentifier());

        return commitNode;
    }
}