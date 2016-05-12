package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.Artifact;
import cz.zcu.kiv.ppicha.spade.indao.ArtifactDAO;

import javax.persistence.EntityManager;

public class ArtifactDAO_JPA extends GenericDAO_JPA<Artifact> implements ArtifactDAO {

    public ArtifactDAO_JPA(EntityManager em){
        super(em, Artifact.class);
    }

    public Artifact save(Artifact artifact) {
        entityManager.getTransaction().begin();

        Artifact ret;
        if(artifact.getId() == 0) {
            entityManager.persist(artifact);
            ret = artifact;
        } else {
            ret =  entityManager.merge(artifact);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
