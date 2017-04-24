package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.Artifact;

public interface ArtifactDAO extends GenericDAO<Artifact> {

    Artifact save(Artifact artifact);

}
