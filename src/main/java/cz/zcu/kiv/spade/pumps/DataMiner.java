package cz.zcu.kiv.spade.pumps;

import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.RelationClass;
import cz.zcu.kiv.spade.domain.enums.Tool;

public abstract class DataMiner {

    protected static final String LINK_FORMAT = "%d %s %d";
    protected static final String SEVERITY_FIELD_NAME = "severity";
    protected static final String PARENT_OF = "parent of";
    protected static final String CHILD_OF = "child of";
    protected static final int PERCENTAGE_MAX = 100;
    protected static final String ATTACHED_TO = "attached to";
    protected static final String HAS_ATTACHED = "has attached";

    private static final String MENTIONS = "mentions";
    private static final String MENTIONED_BY = "mentioned by";
    protected static final String DASH = "-";
    protected static final double MINUTES_IN_HOUR = 60.0;
    protected static final String COMMA = ",";

    protected final DataPump pump;

    protected DataMiner(DataPump pump) {
        this.pump = pump;
    }

    /**
     * transforms the given string leaving only letters converted to lower case if necessary and cutting any other characters
     *
     * @param anyString string to transform
     * @return letters only lower case version of the string
     */
    protected String toLetterOnlyLowerCase(String anyString) {
        StringBuilder compressed = new StringBuilder();
        for (int i = 0; i < anyString.length(); i++) {
            if (Character.isLetter(anyString.charAt(i))) {
                compressed.append(anyString.charAt(i));
            }
        }
        return compressed.toString().toLowerCase();
    }

    /**
     * tries to find a Relation instance used in the Project corresponding with a given string,
     * and if it fails, creates and adds a new one (with a UNASSIGNED class)
     *
     * @param name Relation name
     * @return found or new Relation instance
     */
    protected Relation resolveRelation(String name) {

        for (Relation relation : pump.pi.getRelations()) {
            if (toLetterOnlyLowerCase(name).equals(toLetterOnlyLowerCase(relation.getName()))) {
                relation.setName(name);

                return relation;
            }
        }
        Relation newRelation = new Relation(name, new RelationClassification(RelationClass.UNASSIGNED));
        pump.pi.getRelations().add(newRelation);
        return newRelation;
    }

    /**
     * adds either a new Project-Person-Role to a collection or a new identity to an existing person based on name and email
     *
     * @param identity person's identity
     * @return added person or identified or enhanced (added identity) previously existing one
     */
    public Person addPerson(Identity identity) {
        if (identity == null) return null;
        for (Person person : pump.pi.getProject().getPeople()) {

            boolean foundSimilar = false;

            for (Identity member : person.getIdentities()) {

                if (identity.getExternalId() != null && member.getExternalId() != null) {
                    if (identity.getExternalId().equals(member.getExternalId())) {
                        return person;
                    }
                }

                boolean sameEmail = false;
                boolean sameName = false;

                if (identity.getEmail() != null && !identity.getEmail().isEmpty()) {
                    sameEmail = identity.getEmail().equals(member.getEmail());
                }
                if (!identity.getName().isEmpty()) {
                    sameName = identity.getName().equals(member.getName());
                }

                if (sameEmail && sameName) return person;
                if (sameEmail != sameName) {
                    if (identity.getEmail().equals(member.getEmail()) && identity.getName().equals(member.getName())) {
                        return person;
                    }
                    foundSimilar = true;
                }
                if (!identity.getDescription().isEmpty() &&
                        (identity.getDescription().equals(member.getName()) ||
                                identity.getDescription().equals(member.getDescription()))) {
                    foundSimilar = true;
                }
            }
            if (foundSimilar) {
                person.getIdentities().add(identity);
                if (identity.getName().length() > person.getName().length()) {
                    person.setName(identity.getName());
                }
                if (identity.getDescription().length() > person.getName().length()) {
                    person.setName(identity.getDescription());
                }
                return person;
            }
        }

        Person newPerson = new Person();
        if (identity.getDescription().isEmpty()) {
            newPerson.setName(identity.getName());
        } else {
            newPerson.setName(identity.getDescription());
        }
        newPerson.getIdentities().add(identity);
        if (this instanceof PeopleMiner) {
            Tool tool = pump.getPi().getToolInstance().getTool();
            if (tool.equals(Tool.REDMINE) || tool.equals(Tool.JIRA) || tool.equals(Tool.BUGZILLA)) {
                ((PeopleMiner) this).mineGroups(newPerson, identity.getExternalId());
            }
        }

        pump.pi.getProject().getPeople().add(newPerson);
        return newPerson;
    }


    /**
     * gets a Role instance with a given name
     *
     * @param name role name
     * @return Role instance or null
     */
    protected Role resolveRole(String name) {
        for (Role role : pump.pi.getRoles()) {
            if (name.equals(role.getName())) return role;
        }
        return null;
    }

    protected double minutesToHours(Integer minutes) {
        return minutes / MINUTES_IN_HOUR;
    }

    /**
     * generates a Work Item Relation instance between two given Work Items
     *
     * @param mentioner the item where mentioned was found
     * @param mentionee mentioned item
     */
    protected void generateMentionRelation(WorkItem mentioner, WorkItem mentionee) {
        Relation mentionsRelation = resolveRelation(MENTIONS);
        Relation mentionedByRelation = resolveRelation(MENTIONED_BY);

        mentioner.getRelatedItems().add(new WorkItemRelation(mentionee, mentionsRelation));
        mentionee.getRelatedItems().add(new WorkItemRelation(mentioner, mentionedByRelation));
    }

    protected int getNumberAfterLastDash(String text) {
        return Integer.parseInt(text.substring(text.lastIndexOf(DASH) + 1));
    }

    protected void generateDeletedIssue(int number) {
        WorkUnit deleted = new WorkUnit();
        deleted.setExternalId(pump.getPi().getName() + DASH + number);
        deleted.setNumber(number);
        pump.getPi().getProject().addUnit(deleted);
    }
}
