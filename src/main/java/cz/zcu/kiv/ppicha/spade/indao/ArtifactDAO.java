package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Artifact;

public interface ArtifactDAO extends GenericDAO<Artifact> {

    Artifact save(Artifact artifact);

}
