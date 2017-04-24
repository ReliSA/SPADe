package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.ProjectInstance;

import java.util.Collection;
import java.util.List;

public interface ProjectInstanceDAO extends GenericDAO<ProjectInstance> {

    ProjectInstance save(ProjectInstance pi);

    void deleteByUrl(String projectHandle);

    ProjectInstance findByUrl(String externalId);

    List<String> selectAllUrls();

    Collection<String> selectEnums(String entity);

    Collection<String> selectEnumsByPrjUrl(String entity, String collection, String url);
}
