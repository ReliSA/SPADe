package cz.zcu.kiv.spade.pumps.issuetracking.assembla;

import com.assembla.*;
import com.assembla.client.AssemblaAPI;
import com.assembla.service.TaskRequest;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.ResolutionClass;
import cz.zcu.kiv.spade.domain.enums.SeverityClass;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueMiner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class AssemblaIssueMiner extends IssueMiner<Ticket> {

    private static final String TICKET_STRING = "Ticket";

    private final AssemblaCommentMiner commentMiner;
    private final AssemblaWorklogMiner worklogMiner;
    private final AssemblaChangelogMiner changelogMiner;
    private final AssemblaAttachmentMiner attachmentMiner;

    AssemblaIssueMiner(AssemblaPump pump) {
        super(pump);
        this.commentMiner = new AssemblaCommentMiner(pump);
        this.worklogMiner = new AssemblaWorklogMiner(pump);
        this.changelogMiner = new AssemblaChangelogMiner(pump);
        this.attachmentMiner = new AssemblaAttachmentMiner(pump);
    }

    @Override
    public void mineItems() {
        for (Ticket ticket : ((AssemblaAPI) pump.getRootObject()).tickets(pump.getPi().getExternalId()).getAll().asList()) {
            mineItem(ticket);
        }
    }

    @Override
    protected void mineItem(Ticket ticket) {
        WorkUnit unit = new WorkUnit();
        unit.setExternalId(ticket.getId().toString());
        unit.setName(ticket.getSummary());
        unit.setDescription(ticket.getDescription());
        unit.setCreated(((AssemblaPump) pump).convertDate(ticket.getCreatedOn()));
        unit.setStartDate(unit.getCreated());
        unit.setNumber(ticket.getNumber());
        User author = ((AssemblaAPI) pump.getRootObject()).users(pump.getPi().getExternalId()).get(ticket.getReporterId());
        unit.setAuthor(addPerson(((AssemblaPeopleMiner) pump.getPeopleMiner()).generateIdentity(author)));
        User assignee = ((AssemblaAPI) pump.getRootObject()).users(pump.getPi().getExternalId()).get(ticket.getReporterId());
        unit.setAssignee(addPerson(((AssemblaPeopleMiner) pump.getPeopleMiner()).generateIdentity(assignee)));
        unit.setEstimatedTime(ticket.getTotalWorkingHours());
        unit.setSpentTime(ticket.getTotalInvestedHours());
        unit.setType(resolveType(ticket.getHierarchyType().name()));
        unit.setStatus(resolveStatus(ticket.getStatus()));
        unit.setPriority(resolvePriority(ticket.getPriority().name()));
        unit.setSeverity(resolveSeverity(ticket));
        unit.setResolution(resolveResolution(ticket));
        resolveCategories(unit, ticket);

        pump.getPi().getProject().addUnit(unit);

        attachmentMiner.mineAttachments(unit, attachmentMiner.collectAttachments(ticket.getId()));

        mineHistory(unit, ticket);

        if (ticket.getMilestoneId() != null) {
            Iteration iteration = new Iteration();
            iteration.setExternalId(ticket.getMilestoneId().toString());
            unit.setIteration(iteration);
        }
    }

    @Override
    protected void resolveCategories(WorkUnit unit, Ticket ticket) {
        for (String tag : ticket.getTags()) {
            boolean found = false;
            for (Category category : pump.getPi().getCategories()) {
                if (category.getName().equals(tag)) {
                    unit.getCategories().add(category);
                    found = true;
                    break;
                }
            }
            if (!found) {
                Category newCategory = new Category();
                newCategory.setName(tag);
                unit.getCategories().add(newCategory);
                pump.getPi().getCategories().add(newCategory);
            }
        }
        for (Map.Entry<String, String> field : ticket.getCustomFields().entrySet()) {
            if (field.getKey().toLowerCase().equals("labels") || field.getKey().toLowerCase().equals("component")) {
                if (field.getValue() == null || field.getValue().isEmpty()) continue;
                boolean found = false;
                for (Category category : pump.getPi().getCategories()) {
                    if (category.getName().equals(field.getValue())) {
                        unit.getCategories().add(category);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Category newCategory = new Category();
                    newCategory.setName(field.getValue());
                    unit.getCategories().add(newCategory);
                    pump.getPi().getCategories().add(newCategory);
                }
            }
        }
    }

    private Resolution resolveResolution(Ticket ticket) {
        for (Map.Entry<String, String> field : ticket.getCustomFields().entrySet()) {
            if (field.getKey().toLowerCase().equals("resolution")) {
                if (field.getValue() == null || field.getValue().isEmpty()) continue;
                Resolution resolution = resolveResolution(field.getValue());
                if (resolution != null) return resolution;
                Resolution newResolution = new Resolution(field.getValue(), resolutionDao.findByClass(ResolutionClass.UNASSIGNED));
                pump.getPi().getResolutions().add(newResolution);
                return newResolution;
            }
        }
        return null;
    }

    @Override
    protected Severity resolveSeverity(Ticket ticket) {
        for (Map.Entry<String, String> field : ticket.getCustomFields().entrySet()) {
            if (field.getKey().toLowerCase().equals(SEVERITY_FIELD_NAME)) {
                if (field.getValue() == null || field.getValue().isEmpty()) continue;
                Severity severity = resolveSeverity(field.getValue());
                if (severity != null) return severity;
                Severity newSeverity = new Severity(field.getValue(), severityDao.findByClass(SeverityClass.UNASSIGNED));
                pump.getPi().getSeverities().add(newSeverity);
                return newSeverity;
            }
        }
        return null;
    }

    @Override
    protected void mineWorklogs(WorkUnit unit, Ticket ticket) {
        List<Integer> ids = new ArrayList<>();
        ids.add(ticket.getId());
        double spentTime = 0;
        for (Task worklog : ((AssemblaAPI) pump.getRootObject()).tasks().getAll(new TaskRequest.Builder().ticketIds(ids).build()).asList()) {
            worklogMiner.generateLogTimeConfiguration(unit, spentTime, worklog);
            spentTime += worklog.getHours();
        }
    }

    @Override
    protected void mineComments(WorkUnit unit, Ticket ticket) {
        for (TicketComment comment : ((AssemblaAPI) pump.getRootObject()).ticketComments(pump.getPi().getExternalId()).getAll(ticket.getNumber()).asList()){
            commentMiner.generateUnitCommentConfig(unit, comment);
        }

    }

    @Override
    protected void mineHistory(WorkUnit unit, Ticket ticket) {
        super.mineHistory(unit, ticket);

        //if (ticket.getCompletedDate() != null) generateClosureConfig(unit, ((AssemblaPump) pump).convertDate(ticket.getCompletedDate()));
        for (Event changelog : ((AssemblaAPI) pump.getRootObject()).activity(pump.getPi().getExternalId()).get().asList()) {
            if (changelog.getObject().equals(TICKET_STRING) && changelog.getObjectId().equals(ticket.getId().toString())) {
                changelogMiner.generateModification(unit, changelog);
            }
        }
    }
}
