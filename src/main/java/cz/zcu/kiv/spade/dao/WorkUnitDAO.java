package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.domain.enums.*;

public interface WorkUnitDAO extends GenericDAO<WorkUnit> {

    WorkUnit save(WorkUnit wu);

    int getUnitCountWithNullEnum(String url, String entity);

    int getUnitCountByEnumName(String name, String url, String entity);

    int getUnitCountByPriority(PriorityClass value, String url);

    int getUnitCountByPriority(PrioritySuperClass value, String url);

    int getUnitCountByStatus(StatusClass value, String url);

    int getUnitCountByStatus(StatusSuperClass value, String url);

    int getUnitCountByResolution(ResolutionClass value, String url);

    int getUnitCountByResolution(ResolutionSuperClass o, String url);

    int getUnitCountBySeverity(SeverityClass value, String url);

    int getUnitCountBySeverity(SeveritySuperClass value, String url);

    int getUnitCountByType(WorkUnitTypeClass value, String url);

    int getUnitCountWithNullEnum(String entity);

    int getUnitCountByEnumName(String name, String entity);

    int getUnitCountByPriority(PriorityClass value);

    int getUnitCountByPriority(PrioritySuperClass value);

    int getUnitCountByStatus(StatusClass value);

    int getUnitCountByStatus(StatusSuperClass value);

    int getUnitCountByResolution(ResolutionClass value);

    int getUnitCountByResolution(ResolutionSuperClass o);

    int getUnitCountBySeverity(SeverityClass value);

    int getUnitCountBySeverity(SeveritySuperClass value);

    int getUnitCountByType(WorkUnitTypeClass value);
}
