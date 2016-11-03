package cz.zcu.kiv.spade;

import cz.zcu.kiv.spade.pumps.git.GitPump;

public class Main {

    private static final String PERSISTENCE_UNIT = "cz.zcu.kiv.spade";

    public static void main(String[] args) {

        //EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        //EntityManager em = factory.createEntityManager();

        //em.close();

        //GitPump gitPump = new GitPump("https://github.com/ReliSA/SPADe.git");

        //GitPump gitPump = new GitPump("https://github.com/ReliSA/crce.git");
        //GitPump gitPump = new GitPump("https://github.com/ReliSA/crce-jacc.git");
        //GitPump gitPump = new GitPump("https://github.com/ReliSA/crce-client.git");
        //GitPump gitPump = new GitPump("https://github.com/ReliSA/multijar-to-graphml.git");
        //GitPump gitPump = new GitPump("https://github.com/ReliSA/jar-api-representation.git");

        //GitPump gitPump = new GitPump("https://github.com/grimoirelab/perceval.git");
        GitPump gitPump = new GitPump("https://github.com/siemens/codeface.git");

        gitPump.mineData();
    }
}
