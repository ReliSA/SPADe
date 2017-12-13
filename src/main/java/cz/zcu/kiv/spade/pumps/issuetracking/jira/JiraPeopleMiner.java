package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicUser;
import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rest.client.domain.User;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.Identity;
import cz.zcu.kiv.spade.domain.Person;
import cz.zcu.kiv.spade.domain.Role;
import cz.zcu.kiv.spade.domain.enums.RoleClass;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.PeopleMiner;

import java.net.URI;
import java.util.concurrent.ExecutionException;

class JiraPeopleMiner extends PeopleMiner<BasicUser> {

    private static final String PROJECT_LEAD_ROLE_NAME = "project lead";
    private static final String AT_STRING = " at ";
    private static final String DOT_STRING = " dot ";

    JiraPeopleMiner(JiraPump pump) {
        super(pump);
    }

    @Override
    public void minePeople() {
        Person person = addPerson(generateIdentity(((Project) pump.getSecondaryObject()).getLead()));
        person.getRoles().add(getProjectLeadRole());
    }

    private Role getProjectLeadRole() {
        Role projectLeadRole = resolveRole(PROJECT_LEAD_ROLE_NAME);
        if (projectLeadRole == null) {
            projectLeadRole = new Role(PROJECT_LEAD_ROLE_NAME, roleDao.findByClass(RoleClass.PROJECTMANAGER));
            pump.getPi().getRoles().add(projectLeadRole);
        }
        return projectLeadRole;
    }

    @Override
    protected Identity generateIdentity(BasicUser basicUser) {
        Identity identity = new Identity();
        if (basicUser == null) {
            identity.setName(UNKNOWN_IDENTITY_NAME);
            return identity;
        }
        try {
            User user = ((JiraRestClient) pump.getRootObject()).getUserClient().getUser(basicUser.getName()).get();
            identity.setExternalId(user.getSelf().toString());
            identity.setName(user.getName());
            identity.setDescription(user.getDisplayName());

            String email = user.getEmailAddress().replace(AT_STRING, DataPump.AT);
            email = email.replace(DOT_STRING, DOT);
            identity.setEmail(email);

        } catch (InterruptedException | ExecutionException e) {
            // loss of email
            identity.setExternalId(basicUser.getSelf().toString());
            identity.setName(basicUser.getName());
            identity.setDescription(basicUser.getDisplayName());
        }
        return identity;
    }


    @Override
    protected void mineGroups(Person person, String userId) {
        User user = null;
        try {
            user = ((JiraRestClient) pump.getRootObject()).getUserClient().getUser(URI.create(userId)).get();
        } catch (InterruptedException | ExecutionException e) {
            App.printLogMsg(String.format(USER_PERMISSION_ERR_FORMAT, person.getName()), false);
        }
        if (user == null) return;
        if (user.getGroups().getItems() != null) {
            for (String name : user.getGroups().getItems()) {
                resolveGroup(name).getMembers().add(person);
            }
        }
    }

    @Override
    public void mineGroups() {
        // API doesn't allow for upfront mining of groups associated with the project,
        // therefore mined while mining users (mineGroups(Person, String))
    }
}
