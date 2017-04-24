package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.ArtifactDAO;
import cz.zcu.kiv.spade.domain.Artifact;

import javax.persistence.EntityManager;

public class ArtifactDAO_JPA extends GenericDAO_JPA<Artifact> implements ArtifactDAO {

    public ArtifactDAO_JPA(EntityManager em) {
        super(em, Artifact.class);
    }

    public Artifact save(Artifact artifact) {
        entityManager.getTransaction().begin();

        Artifact ret;
        if (artifact.getId() == 0) {
            entityManager.persist(artifact);
            ret = artifact;
        } else {
            ret = entityManager.merge(artifact);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
