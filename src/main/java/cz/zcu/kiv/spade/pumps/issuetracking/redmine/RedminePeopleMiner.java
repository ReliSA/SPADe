package cz.zcu.kiv.spade.pumps.issuetracking.redmine;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Membership;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.User;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.Group;
import cz.zcu.kiv.spade.domain.Identity;
import cz.zcu.kiv.spade.domain.Person;
import cz.zcu.kiv.spade.pumps.PeopleMiner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class RedminePeopleMiner extends PeopleMiner<User> {

    private static final String MEMBERSHIPS_PERMISSION_ERR_MSG = "Insufficient permissions for memberships";
    private static final String GROUPS_PERMISSION_ERR_MSG = "Insufficient permissions for groups";

    RedminePeopleMiner(RedminePump pump) {
        super(pump);
    }

    @Override
    protected Identity generateIdentity(User user) {
        Identity identity = new Identity();
        if (user == null) {
            identity.setName(UNKNOWN_IDENTITY_NAME);
            return identity;
        }
        identity.setExternalId(user.getId().toString());
        identity.setName(user.getLogin());
        identity.setDescription(user.getFullName());
        identity.setEmail(user.getMail());
        return identity;
    }

    Identity generateIdentity(String login, String name) {
        Identity identity = new Identity();
        if (!login.isEmpty()){
            identity.setName(login);
        }
        identity.setDescription(name);
        return identity;
    }

    @Override
    public void minePeople() {
        List<Membership> memberships = new ArrayList<>();
        try {
            memberships = ((RedmineManager) pump.getRootObject()).getMembershipManager().getMemberships(((Project) pump.getSecondaryObject()).getId());
        } catch (RedmineException e) {
            App.printLogMsg(this, MEMBERSHIPS_PERMISSION_ERR_MSG);
        }
        for (Membership member : memberships) {
            if (member.getUserId() == null) continue;
            Person person = addPerson(generateIdentity(member.getUserId(), member.getUserName()));

            for (com.taskadapter.redmineapi.bean.Role redmineRole : member.getRoles()) {
                person.getRoles().add(resolveRole(redmineRole.getName()));
            }
        }
    }

    /**
     * mines user identity
     *
     * @param id   user ID
     * @param name user name
     * @return Identity instance
     */
    Identity generateIdentity(Integer id, String name) {
        Identity identity = new Identity();
        if (id == null) {
            if (name == null) {
                name = UNKNOWN_IDENTITY_NAME;
            }
            identity.setName(name);
            return identity;
        }
        try {
            User user = ((RedmineManager) pump.getRootObject()).getUserManager().getUserById(id);
            identity.setExternalId(id.toString());
            identity.setName(user.getLogin());
            identity.setDescription(user.getFullName());
            identity.setEmail(user.getMail());
        } catch (RedmineException e) {
            App.printLogMsg(this, String.format(USER_PERMISSION_ERR_FORMAT, id.toString()));
        }
        return identity;
    }

    @Override
    public void mineGroups() {
        Collection<com.taskadapter.redmineapi.bean.Group> redmineGroups = new ArrayList<>();
        try {
            redmineGroups = ((RedmineManager) pump.getRootObject()).getUserManager().getGroups();
        } catch (RedmineException e) {
            App.printLogMsg(this, GROUPS_PERMISSION_ERR_MSG);
        }
        if (redmineGroups.isEmpty()) return;

        for (com.taskadapter.redmineapi.bean.Group redmineGroup : redmineGroups) {
            Group group = resolveGroup(redmineGroup.getName());
            group.setExternalId(redmineGroup.getId().toString());
            group.setDescription(redmineGroup.getStorage().toString());
        }
    }

    @Override
    protected void mineGroups(Person person, String userId) {
        User user = null;
        try {
            user = ((RedmineManager) pump.getRootObject()).getUserManager().getUserById(Integer.parseInt(userId));
        } catch (RedmineException e) {
            App.printLogMsg(this, GROUPS_PERMISSION_ERR_MSG);
        }
        if (user == null) return;

        Collection<com.taskadapter.redmineapi.bean.Group> redmineGroups = user.getGroups();
        for (com.taskadapter.redmineapi.bean.Group redmineGroup : redmineGroups) {
            Group group = resolveGroup(redmineGroup.getName());
            group.setExternalId(redmineGroup.getId().toString());
            group.setDescription(redmineGroup.getStorage().toString());
            group.getMembers().add(person);
        }
    }
}
