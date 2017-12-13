package cz.zcu.kiv.spade.pumps.issuetracking.assembla;

import com.assembla.Ticket;
import com.assembla.User;
import com.assembla.UserRole;
import com.assembla.client.AssemblaAPI;
import cz.zcu.kiv.spade.domain.Identity;
import cz.zcu.kiv.spade.domain.Person;
import cz.zcu.kiv.spade.pumps.PeopleMiner;

class AssemblaPeopleMiner extends PeopleMiner<User> {

    private static final String COMMA = ",";

    AssemblaPeopleMiner(AssemblaPump pump) {
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
        identity.setName(user.getLogin());
        identity.setDescription(user.getName());
        return identity;
    }

    Identity generateIdentity(String userId) {
        User user = ((AssemblaAPI) pump.getRootObject()).users(pump.getPi().getExternalId()).get(userId);
        return generateIdentity(user);
    }

    void mineEmails() {
        for (Ticket ticket : ((AssemblaAPI) pump.getRootObject()).tickets(pump.getPi().getExternalId()).getAll().asList()) {
            for (String notification : ticket.getNotificationList()) {
                String[] parts = notification.split(COMMA);
                for (Person person : pump.getPi().getProject().getPeople()) {
                    for (Identity identity : person.getIdentities()) {
                        if (parts[0].equals(identity.getExternalId())) {
                            identity.setEmail(parts[1]);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void minePeople() {
        for (User user : ((AssemblaAPI) pump.getRootObject()).users(pump.getPi().getExternalId()).getForSpace()) {
            Person person = addPerson(generateIdentity(user));
            for (UserRole userRole : ((AssemblaAPI) pump.getRootObject()).roles(pump.getPi().getExternalId()).getAll()) {
                if (userRole.getUserId().equals(user.getId())) {
                    person.getRoles().add(resolveRole(userRole.getTitle()));
                    person.getRoles().add(resolveRole(userRole.getTitle()));
                }
            }
        }
    }

    @Override
    public void mineGroups() {
        // Assembla does not have groups or teams
    }

    @Override
    protected void mineGroups(Person person, String userId) {
        // Assembla does not have groups or teams
    }
}