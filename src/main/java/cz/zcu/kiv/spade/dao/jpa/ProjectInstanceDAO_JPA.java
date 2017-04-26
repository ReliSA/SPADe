package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.ProjectInstanceDAO;
import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.gui.utils.EnumStrings;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProjectInstanceDAO_JPA extends GenericDAO_JPA<ProjectInstance> implements ProjectInstanceDAO {

    public ProjectInstanceDAO_JPA(EntityManager em) {
        super(em, ProjectInstance.class);
    }

    public ProjectInstance save(ProjectInstance pi) {
        entityManager.getTransaction().begin();

        ProjectInstance ret;
        if (pi.getId() == 0) {
            entityManager.persist(pi);
            ret = pi;
        } else {
            ret = entityManager.merge(pi);
        }

        entityManager.getTransaction().commit();

        return ret;
    }

    @Override
    public void deleteByUrl(String externalId) {
        entityManager.getTransaction().begin();

        ProjectInstance pi = findByUrl(externalId);
        if (pi != null) {
            entityManager.remove(pi);
        }

        entityManager.getTransaction().commit();
    }

    @Override
    public ProjectInstance findByUrl(String url) {
        TypedQuery<ProjectInstance> q = entityManager.createQuery(
                "SELECT pi FROM ProjectInstance pi WHERE pi.url = :url", ProjectInstance.class);
        q.setParameter("url", url);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<String> selectAllUrls() {
        TypedQuery<String> q = entityManager.createQuery(
                "SELECT pi.url FROM ProjectInstance pi ORDER BY pi.url", String.class);
        List<String> results = new ArrayList<>();
        try {
            for (Object o : q.getResultList()) {
                results.add(o.toString());
            }
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public Collection<String> selectEnums(EnumStrings entity) {
        Query q = entityManager.createQuery(
                "SELECT en.name FROM " + entity.getClassName() + " en " +
                        "GROUP BY en.name " +
                        "ORDER BY en.classification.id");
        List<String> results = new ArrayList<>();
        try {
            for (Object o : q.getResultList()) {
                results.add((String) o);
            }
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public Collection<String> selectEnumsByPrjUrl(EnumStrings entity, String url) {
        Query q = entityManager.createQuery(
                "SELECT en.name FROM " + entity.getClassName() + " en, ProjectInstance pi " +
                        "WHERE pi.url = :url " +
                        "AND en MEMBER pi." + entity.getCollectionName() + " " +
                        "ORDER BY en.classification.id"
        );
        q.setParameter("url", url);
        List<String> results = new ArrayList<>();
        try {
            for (Object o : q.getResultList()) {
                results.add((String) o);
            }
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return results;
    }
}
