package cz.zcu.kiv.spade.pumps.vcs.svn;

import cz.zcu.kiv.spade.domain.Identity;
import cz.zcu.kiv.spade.domain.Person;
import cz.zcu.kiv.spade.pumps.PeopleMiner;

class SvnPeopleMiner extends PeopleMiner<String> {

    SvnPeopleMiner(SvnPump pump) {
        super(pump);
    }

    @Override
    protected Identity generateIdentity(String user) {
        Identity identity = new Identity();
        if (user == null) {
            identity.setName(UNKNOWN_IDENTITY_NAME);
            return identity;
        }
        identity.setName(user);
        return identity;
    }

    @Override
    public void minePeople() {
        // mining all people with access to repository is unfeasible, especially for publicly accessible repositories
    }

    @Override
    public void mineGroups() {
        // SVN doesn't have any equivalent of people groups or teams
    }

    @Override
    protected void mineGroups(Person person, String userId) {
        // SVN doesn't have any equivalent of people groups or teams
    }
}
