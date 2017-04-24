package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.WorkUnitDAO;
import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.domain.enums.*;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class WorkUnitDAO_JPA extends GenericDAO_JPA<WorkUnit> implements WorkUnitDAO {

    public WorkUnitDAO_JPA(EntityManager em) {
        super(em, WorkUnit.class);
    }

    public WorkUnit save(WorkUnit wu) {
        entityManager.getTransaction().begin();

        WorkUnit ret;
        if (wu.getId() == 0) {
            entityManager.persist(wu);
            ret = wu;
        } else {
            ret = entityManager.merge(wu);
        }

        entityManager.getTransaction().commit();

        return ret;
    }

    @Override
    public int getUnitCountWithNullEnum(String url, String entity) {
        String diff = entity;
        if (entity.equals("WorkUnitType")) diff = "Type";

        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, ProjectInstance pi, " + entity + " en " +
                        "WHERE wu MEMBER pi.project.units " +
                        "AND pi.url = :url " +
                        "AND wu." + diff.toLowerCase() + " IS NULL"
                , Long.class);
        q.setParameter("url", url);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByEnumName(String value, String url, String entity) {
        String diff = entity;
        if (entity.equals("WorkUnitType")) diff = "Type";

        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, ProjectInstance pi, " + entity + " en " +
                        "WHERE wu MEMBER pi.project.units " +
                        "AND pi.url = :url " +
                        "AND wu." + diff.toLowerCase() + " = en " +
                        "AND wu." + diff.toLowerCase() + ".name = :value"
                , Long.class);
        q.setParameter("url", url);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByPriority(PriorityClass value, String url) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, ProjectInstance pi, Priority en " +
                        "WHERE wu MEMBER pi.project.units " +
                        "AND pi.url = :url " +
                        "AND wu.priority = en " +
                        "AND wu.priority.classification.aClass = :value"
                , Long.class);
        q.setParameter("url", url);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByPriority(PrioritySuperClass value, String url) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, ProjectInstance pi, Priority en " +
                        "WHERE wu MEMBER pi.project.units " +
                        "AND pi.url = :url " +
                        "AND wu.priority = en " +
                        "AND wu.priority.classification.superClass = :value"
                , Long.class);
        q.setParameter("url", url);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByStatus(StatusClass value, String url) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, ProjectInstance pi, Status en " +
                        "WHERE wu MEMBER pi.project.units " +
                        "AND pi.url = :url " +
                        "AND wu.status = en " +
                        "AND wu.status.classification.aClass = :value"
                , Long.class);
        q.setParameter("url", url);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByStatus(StatusSuperClass value, String url) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, ProjectInstance pi, Status pr " +
                        "WHERE wu MEMBER pi.project.units " +
                        "AND pi.url = :url " +
                        "AND wu.status = pr " +
                        "AND wu.status.classification.superClass = :value"
                , Long.class);
        q.setParameter("url", url);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByResolution(ResolutionClass value, String url) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, ProjectInstance pi, Resolution en " +
                        "WHERE wu MEMBER pi.project.units " +
                        "AND pi.url = :url " +
                        "AND wu.resolution = en " +
                        "AND wu.resolution.classification.aClass = :value"
                , Long.class);
        q.setParameter("url", url);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByResolution(ResolutionSuperClass value, String url) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, ProjectInstance pi, Status en " +
                        "WHERE wu MEMBER pi.project.units " +
                        "AND pi.url = :url " +
                        "AND wu.resolution = en " +
                        "AND wu.resolution.classification.superClass = :value"
                , Long.class);
        q.setParameter("url", url);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountBySeverity(SeverityClass value, String url) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, ProjectInstance pi, Severity en " +
                        "WHERE wu MEMBER pi.project.units " +
                        "AND pi.url = :url " +
                        "AND wu.severity = en " +
                        "AND wu.severity.classification.aClass = :value"
                , Long.class);
        q.setParameter("url", url);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountBySeverity(SeveritySuperClass value, String url) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, ProjectInstance pi, Status en " +
                        "WHERE wu MEMBER pi.project.units " +
                        "AND pi.url = :url " +
                        "AND wu.severity = en " +
                        "AND wu.severity.classification.superClass = :value"
                , Long.class);
        q.setParameter("url", url);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByType(WorkUnitTypeClass value, String url) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, ProjectInstance pi, WorkUnitType en " +
                        "WHERE wu MEMBER pi.project.units " +
                        "AND pi.url = :url " +
                        "AND wu.type = en " +
                        "AND wu.type.classification.aClass = :value"
                , Long.class);
        q.setParameter("url", url);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountWithNullEnum(String entity) {
        String diff = entity.toLowerCase();
        if (diff.equals("workunittype")) diff = "type";

        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, " + entity + " en " +
                        "WHERE wu." + diff + " IS NULL"
                , Long.class);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByEnumName(String value, String entity) {
        String diff = entity.toLowerCase();
        if (diff.equals("workunittype")) diff = "type";

        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, " + entity + " en " +
                        "WHERE wu." + diff + " = en " +
                        "AND wu." + diff + ".name = :value"
                , Long.class);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByPriority(PriorityClass value) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, Priority en " +
                        "WHERE wu.priority = en " +
                        "AND wu.priority.classification.aClass = :value"
                , Long.class);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByPriority(PrioritySuperClass value) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, Priority en " +
                        "WHERE wu.priority = en " +
                        "AND wu.priority.classification.superClass = :value"
                , Long.class);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByStatus(StatusClass value) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, Status en " +
                        "WHERE wu.status = en " +
                        "AND wu.status.classification.aClass = :value"
                , Long.class);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByStatus(StatusSuperClass value) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, Status pr " +
                        "WHERE wu.status = pr " +
                        "AND wu.status.classification.superClass = :value"
                , Long.class);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByResolution(ResolutionClass value) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, Resolution en " +
                        "WHERE wu.resolution = en " +
                        "AND wu.resolution.classification.aClass = :value"
                , Long.class);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByResolution(ResolutionSuperClass value) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, Status en " +
                        "WHERE wu.resolution = en " +
                        "AND wu.resolution.classification.superClass = :value"
                , Long.class);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountBySeverity(SeverityClass value) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, Severity en " +
                        "WHERE wu.severity = en " +
                        "AND wu.severity.classification.aClass = :value"
                , Long.class);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountBySeverity(SeveritySuperClass value) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, Status en " +
                        "WHERE wu.severity = en " +
                        "AND wu.severity.classification.superClass = :value"
                , Long.class);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }

    @Override
    public int getUnitCountByType(WorkUnitTypeClass value) {
        TypedQuery<Long> q = entityManager.createQuery(
                "SELECT COUNT(wu.id) FROM WorkUnit wu, WorkUnitType en " +
                        "WHERE wu.type = en " +
                        "AND wu.type.classification.aClass = :value"
                , Long.class);
        q.setParameter("value", value);
        int result;
        try {
            result = Math.toIntExact(q.getSingleResult());
        } catch (NoResultException e) {
            return 0;
        }
        return result;
    }
}