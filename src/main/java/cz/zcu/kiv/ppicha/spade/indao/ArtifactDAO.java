package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Artifact;

/**
 * Created by Petr on 21.1.2016.
 */
public interface ArtifactDAO extends GenericDAO<Artifact> {

    Artifact save(Artifact artifact);

}
