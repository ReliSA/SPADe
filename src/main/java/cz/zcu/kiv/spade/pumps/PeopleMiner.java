package cz.zcu.kiv.spade.pumps;

import cz.zcu.kiv.spade.domain.Group;
import cz.zcu.kiv.spade.domain.Identity;
import cz.zcu.kiv.spade.domain.Person;

public abstract class PeopleMiner<UserObject> extends DataMiner {

    protected static final String USER_PERMISSION_ERR_FORMAT = "Insufficient permissions for user: %s";
    protected static final String UNKNOWN_IDENTITY_NAME = "unknown";

    protected PeopleMiner(DataPump pump) {
        super(pump);
    }

    /**
     * mines user identity
     *
     * @param user user
     * @return Identity instance
     */
    protected abstract Identity generateIdentity(UserObject user);

    /**
     * mines data of users with access to the project
     */
    public abstract void minePeople();

    public abstract void mineGroups();

    protected Group resolveGroup(String name) {
        for (Group group : pump.getPi().getGroups()) {
            if (group.getName().equals(name)) return group;
        }
        Group newGroup = new Group();
        newGroup.setName(name);
        return newGroup;
    }

    protected abstract void mineGroups(Person person, String userId);
}
