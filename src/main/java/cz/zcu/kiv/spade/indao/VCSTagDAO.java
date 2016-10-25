package cz.zcu.kiv.spade.indao;


import cz.zcu.kiv.spade.domain.VCSTag;

public interface VCSTagDAO extends GenericDAO<VCSTag> {

    VCSTag save(VCSTag tag);

}
