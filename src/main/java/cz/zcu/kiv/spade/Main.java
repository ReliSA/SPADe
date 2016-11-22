package cz.zcu.kiv.spade;

import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.pumps.git.GitPump;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.PrintStream;

public class Main {

    private static final String PERSISTENCE_UNIT = "cz.zcu.kiv.spade";

    public static void main(String[] args) {

        /*EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        EntityManager em = factory.createEntityManager();

        em.close();*/

        GitPump gitPump =
                new GitPump("https://github.com/ReliSA/SPADe.git");
                //new GitPump("https://github.com/ReliSA/crce.git");
                //new GitPump("https://github.com/ReliSA/crce-jacc.git");
                //new GitPump("https://github.com/ReliSA/crce-client.git");
                //new GitPump("https://github.com/ReliSA/multijar-to-graphml.git");
                //new GitPump("https://github.com/ReliSA/jar-api-representation.git");

                //new GitPump("https://github.com/grimoirelab/perceval.git");
                //new GitPump("https://github.com/siemens/codeface.git");

        try {
            ProjectInstance pi = gitPump.mineData();
            gitPump.printReport(pi, new PrintStream("D:/reports/" + gitPump.getProjectName() + ".txt", "UTF-8"));
            //gitPump.printWorkItemHistories(pi, System.out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            gitPump.close();
        }
    }
}
