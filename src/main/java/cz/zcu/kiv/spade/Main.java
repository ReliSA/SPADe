package cz.zcu.kiv.spade;

import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.load.Loader;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.git.GitPump;
import cz.zcu.kiv.spade.pumps.github.GitHubPump;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.PrintStream;

public class Main {

    private static final String PERSISTENCE_UNIT = "cz.zcu.kiv.spade";
    private static final String GIT_SUFFIX = ".git";
    private static final String GITHUB_PREFIX = "http://github.com/";

    public static void main(String[] args) {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        EntityManager em = factory.createEntityManager();


        String url =
                "https://github.com/ReliSA/SPADe.git";
                //"https://github.com/ReliSA/crce.git";
                //"https://github.com/ReliSA/crce-jacc.git";
                //"https://github.com/ReliSA/crce-client.git";
                //"https://github.com/ReliSA/multijar-to-graphml.git";
                //"https://github.com/ReliSA/jar-api-representation.git";
                //"https://github.com/grimoirelab/perceval.git";
        //"https://github.com/siemens/codeface.git";

        DataPump pump = null;
        //if (url.startsWith(GITHUB_PREFIX)) {
            //pump = new GitHubPump(url, null, "ppicha", "RATMKoRn48");
        //} else if (url.endsWith(GIT_SUFFIX)) {
            pump = new GitPump(url, null, null, null);
        //}

        ProjectInstance pi = null;
        try {
            pi = pump.mineData();
            pi = pump.printReport(pi, new PrintStream("D:/reports/" + pump.getProjectName() + ".txt", "UTF-8"));
            //pump.printWorkItemHistories(pi, System.out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pump.close();
        }

        Loader loader = new Loader(em);
        loader.loadProjectInstance(pi);

        em.close();

    }
}
