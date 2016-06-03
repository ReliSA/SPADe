package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.ToolProjectInstance;
import cz.zcu.kiv.spade.indao.ToolProjectInstanceDAO;

import javax.persistence.EntityManager;

public class ToolProjectInstanceDAO_JPA extends GenericDAO_JPA<ToolProjectInstance> implements ToolProjectInstanceDAO {

    public ToolProjectInstanceDAO_JPA(EntityManager em) {
        super(em, ToolProjectInstance.class);
    }

    public ToolProjectInstance save(ToolProjectInstance tpi) {
        entityManager.getTransaction().begin();

        ToolProjectInstance ret;
        if (tpi.getId() == 0) {
            entityManager.persist(tpi);
            ret = tpi;
        } else {
            ret = entityManager.merge(tpi);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
