package cz.zcu.kiv.spade.pumps.vcs.svn;

import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.vcs.VcsPump;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import javax.persistence.EntityManager;

public class SvnPump extends VcsPump<SVNRepository, Object> {

    /**
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public SvnPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
        this.tool = Tool.SVN;
        commitMiner = new SvnCommitMiner(this);
        peopleMiner = new SvnPeopleMiner(this);
        releaseMiner = new SvnReleaseMiner(this);
        relationMiner = new SvnRelationMiner(this);
    }

    @Override
    public ProjectInstance mineData(EntityManager em) {
        pi = super.mineData(em);

        setToolInstance();

        try {
            pi.setExternalId(rootObject.getRepositoryUUID(true));
        } catch (SVNException e) {
            e.printStackTrace();
        }

        mineContent();

        return pi;
    }

    @Override
    public void close() {
        rootObject.closeSession();
        super.close();
    }

    @Override
    protected SVNRepository init() {
        SVNRepository repository = null;
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(projectHandle));
            ISVNAuthenticationManager authManager =
                    SVNWCUtil.createDefaultAuthenticationManager(username, password.toCharArray());
            repository.setAuthenticationManager(authManager);
        } catch (SVNException e){
            e.printStackTrace();
        }
        return repository;
    }
}
