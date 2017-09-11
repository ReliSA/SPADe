package cz.zcu.kiv.spade.output;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import cz.zcu.kiv.spade.domain.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CodefacePrinter {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public void print(ProjectInstance pi) {

        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream("csv/" + pi.getProject().getName() + ".csv"), StandardCharsets.UTF_8);
            StatefulBeanToCsvBuilder<CodefaceBean> builder = new StatefulBeanToCsvBuilder<CodefaceBean>(writer)
                    .withSeparator(',').withQuotechar('"').withEscapechar('"');
            StatefulBeanToCsv<CodefaceBean> beanToCsv = builder.build();

            List<CodefaceBean> beans = new ArrayList<>();

            for (WorkUnit wu : pi.getProject().getUnits()){
                if (wu.getStatus().getName().equals("deleted")) continue;

                CodefaceBean bean = new CodefaceBean();

                bean.setExternalId(wu.getExternalId());
                bean.setUrl(wu.getUrl());
                bean.setIssueType(wu.getType().getName());
                bean.setTitle(wu.getName());
                String tmp = wu.getDescription().replace("\r\n", "");
                bean.setDescription(tmp.replace("\n", ""));
                if (wu.getCreated() != null) bean.setCreationDate(FORMAT.format(wu.getCreated()));

                StringBuilder author = new StringBuilder();
                int i = 1;
                if (wu.getAuthor() != null) {
                    for (String email : wu.getAuthor().getEmails()) {
                        author.append(email);
                        if (i < wu.getAuthor().getEmails().size()) author.append("@@");
                        i++;
                    }
                }

                bean.setCreatedBy(author.toString());
                bean.setPriority(wu.getPriority().getName());
                bean.setSeverity(wu.getSeverity().getName());
                if (wu.getIteration() != null) bean.setVersion(wu.getIteration().getName());
                if (wu.getStartDate() != null) bean.setStartDate(FORMAT.format(wu.getStartDate()));
                if (wu.getDueDate() != null) bean.setDueDate(FORMAT.format(wu.getDueDate()));

                StringBuilder asignee = new StringBuilder();
                i = 1;
                if (wu.getAssignee() != null) {
                    for (String email : wu.getAssignee().getEmails()) {
                        asignee.append(email);
                        if (i < wu.getAssignee().getEmails().size()) asignee.append("@@");
                        i++;
                    }
                }

                bean.setAssignedTo(asignee.toString());
                bean.setEstimateTime(wu.getEstimatedTime() + "");
                bean.setSpentTime(wu.getSpentTime() + "");
                bean.setProgress(wu.getProgress() + "");
                bean.setStatus(wu.getStatus().getName());
                if (wu.getResolution() != null) bean.setResolution(wu.getResolution().getName());

                StringBuilder categories = new StringBuilder();
                i = 1;
                for (Category category : wu.getCategories()) {
                    categories.append(category.getName());
                    if (i < wu.getCategories().size()) categories.append(";");
                    i++;
                }

                bean.setCategory(categories.toString());

                StringBuilder related = new StringBuilder();
                i = 1;
                for (WorkItemRelation wir : wu.getRelatedItems()) {
                    if (wir.getRelatedItem() instanceof Commit) {
                        related.append(((Commit) wir.getRelatedItem()).getIdentifier());
                        if (i < wu.getRelatedItems().size()) related.append(";");
                    }
                    i++;
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
                        String tmp = con.getDescription().replace("\r\n", "");
                        bean.setDescription(tmp.replace("\n", ""));
                        bean.setCreationDate(FORMAT.format(con.getCreated()));

                        StringBuilder author = new StringBuilder();
                        int i = 1;
                        for (String email : con.getAuthor().getEmails()) {
                            author.append(email);
                            if (i < con.getAuthor().getEmails().size()) author.append("@@");
                            i++;
                        }

                        bean.setCreatedBy(author.toString());

                        beans.add(bean);
                    }
                }
            }

            beanToCsv.write(beans);

            writer.flush();
            writer.close();
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
        }
    }

    public void print(Collection<CodefaceBean> beans, String name) {

        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream("csv/" + name + ".csv"), StandardCharsets.UTF_8);
            StatefulBeanToCsvBuilder<CodefaceBean> builder = new StatefulBeanToCsvBuilder<CodefaceBean>(writer)
                    .withSeparator(',').withQuotechar('"').withEscapechar('"');
            StatefulBeanToCsv<CodefaceBean> beanToCsv = builder.build();

            List<CodefaceBean> list = new ArrayList<>();
            list.addAll(beans);
            beanToCsv.write(list);

            writer.flush();
            writer.close();
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
        }
    }
}