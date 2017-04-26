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

    public void print(ProjectInstance pi) throws JSONException {

        Collection<JSONObject> nodes = new ArrayList<>();
        Collection<JSONObject> edges = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        int nodesId = 0;
        Map<WorkItem, Integer> itemMap = new HashMap<>();

        for (WorkItem item : pi.getProject().getAllItems()) {
            JSONObject itemNode = new JSONObject();
            itemNode.put("id", nodesId);
            itemNode.put("name", item.getName());
            itemNode.put("description", item.getDescription());
            if (item.getCreated() != null)
                itemNode.put("begin", format.format(item.getCreated()));
            if (item instanceof WorkUnit) {
                itemNode.put("stereotype", "person");
                WorkUnit unit = (WorkUnit) item;
                if (unit.getDueDate() != null)
                    itemNode.put("end", format.format(unit.getDueDate()));

            } else if (item instanceof Commit || item instanceof CommittedConfiguration || item instanceof Configuration) {
                itemNode.put("stereotype", "device");
                if (item instanceof Commit || item instanceof  CommittedConfiguration) {
                    CommittedConfiguration configuration = (CommittedConfiguration) item;
                    if (configuration.getCommitted() != null)
                        itemNode.put("end", format.format(configuration.getCommitted()));
                }

            } else if (item instanceof Artifact) {
                itemNode.put("stereotype", "theory");
            }
            JSONObject properties = new JSONObject();
            properties.put("startPrecision", "day");
            properties.put("endPrecision", "day");
            itemNode.put("properties", properties);
            nodes.add(itemNode);
            itemMap.put(item, nodesId++);
        }

        int edgesId = 1;

        for (WorkItem item : pi.getProject().getAllItems()) {
            for (WorkItemRelation relation : item.getRelatedItems()) {
                JSONObject relNode = new JSONObject();
                relNode.put("id", edgesId++);
                relNode.put("stereotype", "relationship");
                if (relation.getRelation() != null) relNode.put("name", relation.getRelation().getName());
                else relNode.put("name", "relates to");
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
                    relNode.put("stereotype", "relationship");
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
                                new FileOutputStream("Timeline/software/data/data.js")
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
}