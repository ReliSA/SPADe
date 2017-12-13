package cz.zcu.kiv.spade.pumps.issuetracking.bugzilla;

import b4j.core.Component;
import b4j.core.DefaultIssue;
import b4j.core.IssueLink;
import b4j.core.Project;
import cz.zcu.kiv.spade.domain.Category;
import cz.zcu.kiv.spade.domain.Relation;
import cz.zcu.kiv.spade.domain.Severity;
import cz.zcu.kiv.spade.domain.enums.RelationClass;
import cz.zcu.kiv.spade.domain.enums.SeverityClass;
import cz.zcu.kiv.spade.domain.enums.WorkUnitTypeClass;
import cz.zcu.kiv.spade.pumps.issuetracking.EnumsMiner;

class BugzillaEnumsMiner extends EnumsMiner {

    private static final String DEPENDS_ON = "DEPENDS_ON";
    private static final String DUPLICATE = "DUPLICATE";
    private static final String UNSPECIFIED = "UNSPECIFIED";

    BugzillaEnumsMiner(BugzillaPump pump) {
        super(pump);
    }

    @Override
    protected void minePriorities() {
        // API doesn't list priorities in project, therefore handled while mining issues (mineItems)
    }

    @Override
    protected void mineWUTypes() {
        // API doesn't list types in project, therefore handled while mining issues (mineItems)
    }

    @Override
    protected void mineResolutions() {
        // API doesn't list resolutions in project, therefore handled while mining issues (mineItems)
    }

    @Override
    protected void mineWURelationTypes() {
        for (IssueLink.Type link : IssueLink.Type.values()) {
            addRelation(link.name());
        }
        addRelation(PARENT_OF);
        addRelation(CHILD_OF);
    }

    private void addRelation(String name) {
        boolean found = false;
        for (Relation relation : pump.getPi().getRelations()) {
            if (toLetterOnlyLowerCase(relation.getName()).equals(toLetterOnlyLowerCase(name))) {
                relation.setName(name);
                found = true;
                break;
            }
        }
        if (!found) {
            RelationClass aClass;
            switch (name) {
                case CHILD_OF:
                case PARENT_OF:
                    aClass = RelationClass.PARENTOF;
                    break;
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
            Relation newRelation = new Relation(name, relationDao.findByClass(aClass));
            pump.getPi().getRelations().add(newRelation);
        }
    }

    @Override
    protected void mineStatuses() {
        // API doesn't list statuses in project, therefore handled while mining issues (mineItems)
    }

    @Override
    protected void mineSeverities() {
        for (String name : DefaultIssue.SEVERITIES) {
            boolean found = false;
            for (Severity severity : pump.getPi().getSeverities()) {
                if (toLetterOnlyLowerCase(severity.getName()).equals(toLetterOnlyLowerCase(name))) {
                    severity.setName(name);
                    found = true;
                    break;
                }
            }
            if (!found && !name.equals(WorkUnitTypeClass.ENHANCEMENT.name().toLowerCase())) {
                Severity newSeverity = new Severity(name, severityDao.findByClass(SeverityClass.UNASSIGNED));
                pump.getPi().getSeverities().add(newSeverity);
            }
        }
    }

    @Override
    protected void mineCategories() {
        Iterable<Component> components = ((Project) pump.getSecondaryObject()).getComponents();
        for (Component component : components) {
            Category category = new Category();
            category.setExternalId(component.getId());
            category.setName(component.getName());
            category.setDescription(component.getDescription());
            pump.getPi().getCategories().add(category);
        }
    }

    @Override
    protected void mineRoles() {
        // API doesn't list roles in project, therefore handled while mining issues (mineItems)
    }
}
