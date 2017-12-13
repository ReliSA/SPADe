package cz.zcu.kiv.spade.pumps.issuetracking.bugzilla;

import b4j.core.Issue;
import b4j.core.IssueLink;
import b4j.core.session.bugzilla.BugzillaClient;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.RelationClass;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueTrackingRelationMiner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

class BugzillaRelationMiner extends IssueTrackingRelationMiner {

    private static final String ISSUE_NOT_EXISTS_ERR_FORMAT = "issue %s does not exist";
    private static final String ID_CRITERION = "id";

    BugzillaRelationMiner(BugzillaPump pump) {
        super(pump);
    }

    @Override
    protected void mineMentions() {
        // from work unit descriptions
        for (WorkUnit unit : pump.getPi().getProject().getUnits()) {
            mineAllMentionedItems(unit);
        }

        for (Configuration configuration : pump.getPi().getProject().getConfigurations()) {
            if (!(configuration instanceof Commit)) {
                for (WorkItemChange change : configuration.getChanges()) {
                    // from work unit comments and time logs
                    if (change.getChangedItem() instanceof WorkUnit &&
                            (change.getType().equals(WorkItemChange.Type.COMMENT) ||
                                    change.getType().equals(WorkItemChange.Type.LOGTIME))) {
                        mineAllMentionedItems(change.getChangedItem(), configuration.getDescription());
                    }
                }
            }
        }
    }

    @Override
    protected void mineRelations() {
        for (WorkUnit unit : pump.getPi().getProject().getUnits()) {
            Map<String, Object> criteria = new HashMap<>();
            criteria.put(BugzillaPump.PRODUCT_CRITERION, pump.getPi().getName());
            criteria.put(ID_CRITERION, unit.getNumber());
            criteria.put(BugzillaPump.LIMIT_CRITERION, 1);
            Iterable<Issue> issues = null;
            try {
                issues = ((BugzillaClient) pump.getRootObject()).getBugClient().findBugs(criteria).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                App.printLogMsg(String.format(ISSUE_NOT_EXISTS_ERR_FORMAT, unit.getExternalId()), false);
                continue;
            }
            if (issues != null) {
                for (Issue issue : issues) {
                    if (issue.getLinks() != null) {
                        for (IssueLink link : issue.getLinks()) {
                            WorkUnit related = pump.getPi().getProject().getUnit(link.getLinkTypeDescription());
                            unit.getRelatedItems().add(new WorkItemRelation(related, resolveRelation(link)));
                        }
                    }
                    if (issue.getChildCount() > 0) {
                        for (Issue childIssue : issue.getChildren()) {
                            WorkUnit child = pump.getPi().getProject().getUnit(childIssue.getId());
                            App.printLogMsg(String.format(PARENT_CHILD_FORMAT, unit.getNumber(), child.getNumber()), false);
                            unit.getRelatedItems().add(new WorkItemRelation(child, resolveRelation(PARENT_OF)));
                            child.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(CHILD_OF)));
                        }
                    }
                }
            }
        }
    }



    private Relation resolveRelation(IssueLink link) {

        // LinkTypeName already in relations -> return this
        for (Relation relation : pump.getPi().getRelations()) {
            if (toLetterOnlyLowerCase(link.getLinkTypeName()).equals(toLetterOnlyLowerCase(relation.getName()))) {
                relation.setName(link.getLinkTypeName());
                return relation;
            }
        }

        // LinkType.name already in relations -> assign its classification to this
        for (Relation relation : pump.getPi().getRelations()) {
            if (toLetterOnlyLowerCase(link.getLinkType().name()).equals(toLetterOnlyLowerCase(relation.getName()))) {
                Relation newRelation = new Relation(link.getLinkTypeName(), relationDao.findByClass(relation.getAClass()));
                pump.getPi().getRelations().add(newRelation);
                return newRelation;
            }
        }

        // completely new relation
        RelationClass aClass;
        switch (link.getLinkType()) {
            case DEPENDS_ON:
                aClass = RelationClass.BLOCKEDBY;
                break;
            case DUPLICATE:
                aClass = RelationClass.DUPLICATES;
                break;
            case UNSPECIFIED:
            default:
                aClass = RelationClass.UNASSIGNED;
                break;
        }
        Relation newRelation = new Relation(link.getLinkTypeName(), relationDao.findByClass(aClass));
        pump.getPi().getRelations().add(newRelation);
        return newRelation;
    }
}
