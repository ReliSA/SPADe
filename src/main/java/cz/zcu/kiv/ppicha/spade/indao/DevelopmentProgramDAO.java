package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.DevelopmentProgram;

public interface DevelopmentProgramDAO extends GenericDAO<DevelopmentProgram> {

    DevelopmentProgram save(DevelopmentProgram devProg);

}
