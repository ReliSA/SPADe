package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.DevelopmentProgram;

public interface DevelopmentProgramDAO extends GenericDAO<DevelopmentProgram> {

    DevelopmentProgram save(DevelopmentProgram devProg);

}
