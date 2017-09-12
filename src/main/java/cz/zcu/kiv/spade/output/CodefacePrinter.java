package cz.zcu.kiv.spade.output;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import cz.zcu.kiv.spade.domain.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CodefacePrinter {

    public void print(ProjectInstance pi) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        List<CodefaceBean> beans = new ArrayList<>();

        for (WorkUnit wu : pi.getProject().getUnits()){
            if (wu.getStatus().getName().equals("deleted")) continue;

            CodefaceBean bean = new CodefaceBean();

            bean.setExternalId(wu.getExternalId());
            bean.setUrl(wu.getUrl());
            bean.setIssueType(wu.getType().getName());
            bean.setTitle(wu.getName());
            String tmp = wu.getDescription().replace("\r\n", " ");
            bean.setDescription(tmp.replace("\n", " "));
            if (wu.getCreated() != null) bean.setCreationDate(format.format(wu.getCreated()));

            StringBuilder author = new StringBuilder();
            if (wu.getAuthor() != null) {
                for (String email : wu.getAuthor().getEmails()) {
                    if (!author.toString().isEmpty()) author.append("@@");
                    author.append(email);
                }
            }

            bean.setCreatedBy(author.toString());
            bean.setPriority(wu.getPriority().getName());
            bean.setSeverity(wu.getSeverity().getName());
            if (wu.getIteration() != null) bean.setVersion(wu.getIteration().getName());
            if (wu.getStartDate() != null) bean.setStartDate(format.format(wu.getStartDate()));
            if (wu.getDueDate() != null) bean.setDueDate(format.format(wu.getDueDate()));

            StringBuilder asignee = new StringBuilder();
            if (wu.getAssignee() != null) {
                for (String email : wu.getAssignee().getEmails()) {
                    if (email.equals("unknown")) continue;
                    if (!asignee.toString().isEmpty()) asignee.append("@@");
                    asignee.append(email);
                }
            }

            bean.setAssignedTo(asignee.toString());
            bean.setEstimateTime(wu.getEstimatedTime() + "");
            bean.setSpentTime(wu.getSpentTime() + "");
            bean.setProgress(wu.getProgress() + "");
            bean.setStatus(wu.getStatus().getName());
            if (wu.getResolution() != null) bean.setResolution(wu.getResolution().getName());

            StringBuilder categories = new StringBuilder();
            for (Category category : wu.getCategories()) {
                if (!categories.toString().isEmpty()) categories.append(";");
                categories.append(category.getName());
            }

            bean.setCategory(categories.toString());

            StringBuilder related = new StringBuilder();
            for (WorkItemRelation wir : wu.getRelatedItems()) {
                if (wir.getRelatedItem() instanceof Commit) {
                    if (!related.toString().isEmpty()) related.append(";");
                    related.append(((Commit) wir.getRelatedItem()).getIdentifier());
                }
            }

            bean.setCommits(related.toString());

            beans.add(bean);
        }

        for (Configuration con : pi.getProject().getConfigurations()) {
            for (WorkItemChange wic : con.getChanges()) {
                if (wic.getChangedItem() instanceof WorkUnit && wic.getName().equals("COMMENT")) {
                    WorkUnit wu = (WorkUnit) wic.getChangedItem();

                    CodefaceBean bean = new CodefaceBean();

                    bean.setExternalId(wu.getExternalId());
                    bean.setIssueType("COMMENT");
                    String tmp = con.getDescription().replace("\r\n", " ");
                    bean.setDescription(tmp.replace("\n", " "));
                    bean.setCreationDate(format.format(con.getCreated()));

                    StringBuilder author = new StringBuilder();
                    for (String email : con.getAuthor().getEmails()) {
                        if (!author.toString().isEmpty()) author.append("@@");
                        author.append(email);
                    }

                    bean.setCreatedBy(author.toString());

                    beans.add(bean);
                }
            }
        }

        this.print(beans, pi.getName());
    }

    public void print(List<CodefaceBean> beans, String name) {
        ColumnPositionMappingStrategy<CodefaceBean> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(CodefaceBean.class);
        String[] columns = new String[]{"externalId", "url", "issueType", "title", "creationDate", "createdBy",
                "priority", "severity", "version", "startDate", "dueDate", "assignedTo", "estimateTime", "spentTime",
                "progress", "status", "resolution", "category", "commits", "description"};
        strategy.setColumnMapping(columns);

        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream("csv/" + name + ".csv"), StandardCharsets.UTF_8);
            StatefulBeanToCsvBuilder<CodefaceBean> builder = new StatefulBeanToCsvBuilder<CodefaceBean>(writer)
                    .withSeparator(',').withQuotechar('"').withEscapechar('"').withMappingStrategy(strategy);
            StatefulBeanToCsv<CodefaceBean> beanToCsv = builder.build();

            beanToCsv.write(beans);

        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
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