package cz.zcu.kiv.spade.pumps.issuetracking.bugzilla;

import b4j.core.User;
import b4j.core.session.bugzilla.BugzillaClient;
import cz.zcu.kiv.spade.domain.Identity;
import cz.zcu.kiv.spade.domain.Person;
import cz.zcu.kiv.spade.pumps.PeopleMiner;

import java.util.concurrent.ExecutionException;

class BugzillaPeopleMiner extends PeopleMiner<User> {

    BugzillaPeopleMiner(BugzillaPump pump) {
        super(pump);
    }

    @Override
    protected Identity generateIdentity(User user) {
        Identity identity = new Identity();
        if (user == null) {
            identity.setName(UNKNOWN_IDENTITY_NAME);
            return identity;
        }
        identity.setExternalId(user.getId());
        identity.setName(user.getName());
        identity.setDescription(user.getRealName());
        return identity;
    }

    @Override
    public void minePeople() {
        // API doesn't list users in project, therefore handled while mining issues (mineItems)
    }

    @Override
    public void mineGroups() {
        // API doesn't list groups in project, therefore handled while mining users (mineGroups(Person, String))
    }

    @Override
    protected void mineGroups(Person person, String userId) {
        User user = null;
        try {
            Iterable<User> users = ((BugzillaClient) pump.getRootObject()).getUserClient().getUsers(Long.parseLong(userId)).get();
            for (User u : users) {
                user = u;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (user == null) return;

        if (user.getTeam() != null) {
            resolveGroup(user.getTeam().getName()).getMembers().add(person);
        }
    }
}
