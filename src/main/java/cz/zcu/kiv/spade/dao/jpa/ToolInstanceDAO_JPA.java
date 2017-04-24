package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.ToolInstanceDAO;
import cz.zcu.kiv.spade.domain.ToolInstance;
import cz.zcu.kiv.spade.domain.enums.Tool;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class ToolInstanceDAO_JPA extends GenericDAO_JPA<ToolInstance> implements ToolInstanceDAO {

    public ToolInstanceDAO_JPA(EntityManager em) {
        super(em, ToolInstance.class);
    }

    public ToolInstance save(ToolInstance ti) {
        entityManager.getTransaction().begin();

        ToolInstance ret;
        if (ti.getId() == 0) {
            entityManager.persist(ti);
            ret = ti;
        } else {
            ret = entityManager.merge(ti);
        }

        entityManager.getTransaction().commit();

        return ret;
    }

    @Override
    public ToolInstance findByToolInstance(String externalId, Tool tool) {
        TypedQuery<ToolInstance> q = entityManager.createQuery(
                "SELECT ti FROM ToolInstance ti WHERE ti.externalId = :externalId AND ti.tool = :tool", ToolInstance.class);
        q.setParameter("externalId", externalId);
        q.setParameter("tool", tool);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Tool findToolByProjectInstanceUrl(String url) {
        TypedQuery<Tool> q = entityManager.createQuery(
                "SELECT ti.tool FROM ProjectInstance pi, ToolInstance ti WHERE pi.toolInstance =  ti.id AND pi.url = :url", Tool.class);
        q.setParameter("url", url);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}