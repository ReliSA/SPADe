package cz.zcu.kiv.spade.pumps.issuetracking.github;

import cz.zcu.kiv.spade.domain.Group;
import cz.zcu.kiv.spade.domain.Identity;
import cz.zcu.kiv.spade.domain.Person;
import cz.zcu.kiv.spade.domain.enums.RoleClass;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.PeopleMiner;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

class GitHubPeopleMiner extends PeopleMiner<GHUser> {

    GitHubPeopleMiner(GitHubPump pump) {
        super(pump);
    }

    @Override
    protected Identity generateIdentity(GHUser user) {
        Identity identity = new Identity();
        if (user == null) {
            identity.setName(UNKNOWN_IDENTITY_NAME);
            return identity;
        }

        identity.setExternalId(Long.toString(user.getId()));
        if (user.getLogin() != null && !user.getLogin().isEmpty()) {
            identity.setName(user.getLogin());
        }

        String name, email;
        while (true) {
            try {
                name = user.getName();
                email = user.getEmail();
                break;
            } catch (IOException e) {
                ((GitHubPump) pump).resetRootObject();
            }
        }

        if (name != null && !name.isEmpty()) {
            identity.setDescription(name);
        }
        if (email != null && !email.isEmpty()) {
            if (identity.getName().isEmpty() || identity.getName().equals(UNKNOWN_IDENTITY_NAME)) {
                identity.setName(email.split(DataPump.AT)[0]);
            }
            identity.setEmail(email);
        }

        return identity;
    }

    @Override
    public void minePeople() {
        try {
            Person person = addPerson(generateIdentity(((GHRepository) pump.getRootObject()).getOwner()));
            person.getRoles().add(resolveRole(GitHubPump.GitHubRole.owner.name()));
        } catch (IOException e) {
            ((GitHubPump) pump).resetRootObject();
        }
        if (((GHRepository) pump.getRootObject()).hasPushAccess()) {
            try {
                for (GHUser collaborator : ((GHRepository) pump.getRootObject()).listCollaborators()) {
                    Person person = addPerson(generateIdentity(collaborator));
                    person.getRoles().add(resolveRole(GitHubPump.GitHubRole.collaborator.name()));
                }
            } catch (IOException e) {
                ((GitHubPump) pump).resetRootObject();
            }
        }
        try {
            for (GHUser contributor : ((GHRepository) pump.getRootObject()).listContributors()) {
                Person person = addPerson(generateIdentity(contributor));
                person.getRoles().add(resolveRole(GitHubPump.GitHubRole.contributor.name()));
            }
        } catch (IOException e) {
            ((GitHubPump) pump).resetRootObject();
        }
        // TODO watch out for resolveRole
        try {
            for (GHUser assignee : ((GHRepository) pump.getRootObject()).listAssignees()) {
                Person person = addPerson(generateIdentity(assignee));
                person.getRoles().add(resolveRole(RoleClass.TEAMMEMBER.name()));
            }
        } catch (IOException e) {
            ((GitHubPump) pump).resetRootObject();
        }
    }

    @Override
    public void mineGroups() {
        Set<GHTeam> teams = new LinkedHashSet<>();
        try {
            teams = ((GitHubPump) pump).getRootObject().getTeams();
        } catch (IOException e) {
            ((GitHubPump) pump).resetRootObject();
        }

        for (GHTeam team : teams) {
            Group group = resolveGroup(team.getName());
            group.setExternalId(Integer.toString(team.getId()));
            group.setDescription(team.getSlug());

            Set<GHUser> members = new LinkedHashSet<>();
            try {
                members = team.getMembers();
            } catch (IOException e) {
                ((GitHubPump) pump).resetRootObject();
            }

            for (GHUser user : members) {

                /*Map<String, GHRepository> repositoryMap = new HashMap<>();
                try {
                    repositoryMap = user.getRepositories();
                } catch (IOException e) {
                    ((GitHubPump) pump).resetRootObject();
                }

                if (!repositoryMap.isEmpty() && repositoryMap.get(pump.getPi().getExternalId()) != null) {*/
                group.getMembers().add(addPerson(generateIdentity(user)));
                //}
            }
            pump.getPi().getGroups().add(group);
        }
    }

    @Override
    protected void mineGroups(Person person, String userId) {
        // API does allow for upfront mining of teams associated with the project,
        // therefore mined separately in mineGroups()
    }
}
