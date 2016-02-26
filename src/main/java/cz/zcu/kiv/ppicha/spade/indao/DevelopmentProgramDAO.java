package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.DevelopmentProgram;

/**
 * Created by Petr on 21.1.2016.
 */
public interface DevelopmentProgramDAO extends GenericDAO<DevelopmentProgram> {

    DevelopmentProgram save(DevelopmentProgram devProg);

}
