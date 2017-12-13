package cz.zcu.kiv.spade.pumps.vcs.git;

import cz.zcu.kiv.spade.domain.Identity;
import cz.zcu.kiv.spade.domain.Person;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.PeopleMiner;
import org.eclipse.jgit.lib.PersonIdent;

class GitPeopleMiner extends PeopleMiner<PersonIdent> {

    GitPeopleMiner(GitPump pump) {
        super(pump);
    }

    @Override
    protected Identity generateIdentity(PersonIdent user) {
        Identity identity = new Identity();
        if (user == null) {
            identity.setName(UNKNOWN_IDENTITY_NAME);
            return identity;
        }

        if (user.getName() != null && !user.getName().isEmpty()) {
            identity.setName(user.getName());
        } else {
            identity.setName(user.getEmailAddress().split(DataPump.AT)[0]);
        }
        identity.setEmail(user.getEmailAddress());

        if (identity.getName().equals(UNKNOWN_IDENTITY_NAME)) {
            identity.setName(user.getEmailAddress().split(DataPump.AT)[0]);
        }

        return identity;
    }

    @Override
    public void minePeople() {
        // mining all people with access to repository is unfeasible, especially for publicly accessible repositories
    }

    @Override
    public void mineGroups() {
        // Git doesn't have any equivalent of people groups or teams
    }

    @Override
    protected void mineGroups(Person person, String userId) {
        // Git doesn't have any equivalent of people groups or teams
    }
}
