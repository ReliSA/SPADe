package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.gui.utils.EnumStrings;

import java.util.Collection;
import java.util.List;

public interface ProjectInstanceDAO extends GenericDAO<ProjectInstance> {

    ProjectInstance save(ProjectInstance pi);

    void deleteByUrl(String projectHandle);

    ProjectInstance findByUrl(String externalId);

    List<String> selectAllUrls();

    Collection<String> selectEnums(EnumStrings entity);

    Collection<String> selectEnumsByPrjUrl(EnumStrings entity, String url);
}
