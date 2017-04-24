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

    public TimelineFilePrinter() {
    }

    public void print(ProjectInstance pi) throws JSONException {
        /*
        Collection<JSONObject> nodes = new ArrayList<>();
        Collection<JSONObject> edges = new ArrayList<>();

        int nodesId = 0;
        int edgesId = 1;
        Map<Integer, Integer> unitIdMap = new HashMap<>();

        for (WorkUnit unit : pi.getProject().getUnits()) {
            if (unit.getStartDate() == null || unit.getDueDate() == null) continue;
            JSONObject unitNode = new JSONObject();
            unitNode.put("id", nodesId);
            unitNode.put("stereotype", "person");
            unitNode.put("name", "#" + unit.getNumber() + " " + unit.getName());
            unitNode.put("description", unit.getDescription());
            unitNode.put("begin", convertDate(unit.getStartDate()));
            unitNode.put("end", convertDate(unit.getDueDate()));
            nodes.add(unitNode);
            unitIdMap.put(unit.getNumber(), nodesId++);
        }
        for (Configuration conf : pi.getProject().getConfigurations()) {
            JSONObject confNode = new JSONObject();
            confNode.put("id", nodesId);
            confNode.put("stereotype", "device");
            confNode.put("name", conf.getName());
            confNode.put("description", conf.getDescription());
            confNode.put("begin", convertDate(conf.getCreated()));
            nodes.add(confNode);
            int confId = nodesId++;

            for (WorkItemChange change : conf.getChanges()) {
                WorkItem item = change.getChangedItem();
                if (item instanceof Artifact) {
                    if (item.getCreated() == null) continue;
                    JSONObject artNode = new JSONObject();
                    artNode.put("id", nodesId);
                    artNode.put("stereotype", "theory");
                    artNode.put("name", item.getName());
                    artNode.put("description", item.getDescription());
                    artNode.put("begin", convertDate(item.getCreated()));
                    nodes.add(artNode);

                    JSONObject confArt = new JSONObject();
                    confArt.put("id", edgesId++);
                    confArt.put("stereotype", "relationship");
                    confArt.put("from", confId);
                    confArt.put("to", nodesId);
                    confArt.put("name", "changes");
                    edges.add(confArt);

                    nodesId++;
                } else {
                    WorkUnit unit = (WorkUnit) item;
                    if (!unitIdMap.containsKey(unit.getNumber())) continue;
                    JSONObject confUnit = new JSONObject();
                    confUnit.put("id", edgesId++);
                    confUnit.put("stereotype", "relationship");
                    confUnit.put("from", confId);
                    confUnit.put("to", unitIdMap.get(unit.getNumber()));
                    confUnit.put("name", "changes");
                    edges.add(confUnit);
                }
            }
            for (WorkItemRelation relation : conf.getRelatedItems()) {
                if (!unitIdMap.containsKey(item.getNumber())) continue;
                JSONObject ref = new JSONObject();
                ref.put("id", edgesId++);
                ref.put("stereotype", "relationship");
                ref.put("from", confId);
                ref.put("to", unitIdMap.get(item.getNumber()));
                ref.put("name", "relates to");
                edges.add(ref);
            }
        }

        JSONObject root = new JSONObject();
        root.put("nodes", nodes);
        root.put("edges", edges);

        try {
            PrintWriter pw = new PrintWriter(
                                new OutputStreamWriter(
                                new FileOutputStream("Timeline/software/data/data.js")
                                , StandardCharsets.UTF_8));
            pw.print("define([],function(){return");
            pw.print(root);
            pw.print(";});");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("define([],function(){return");
        System.out.println(root);
        System.out.println(";});");
        */
    }

    private String convertDate(Date startDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS");
        return format.format(startDate);
    }
}
