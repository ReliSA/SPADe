package cz.zcu.kiv.spade.pumps.issuetracking.github;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.pumps.issuetracking.WikiMiner;
import cz.zcu.kiv.spade.pumps.vcs.git.GitPump;
import org.kohsuke.github.GHRepository;

import java.util.HashMap;
import java.util.Map;

class GitHubWikiMiner extends WikiMiner {

    /** suffix for mining GitHub wiki projects */
    private static final String GITHUB_WIKI_SUFFIX = "/wiki";

    GitHubWikiMiner(GitHubPump pump) {
        super(pump);
    }

    @Override
    public void mineWiki() {
        if (!((GHRepository) pump.getRootObject()).hasWiki()) return;

        String wikiUrl = pump.getPi().getUrl().substring(0, pump.getPi().getUrl().lastIndexOf(App.GIT_SUFFIX)) + GITHUB_WIKI_SUFFIX + App.GIT_SUFFIX;
        GitPump wikiPump = new GitPump(wikiUrl, null, null, null);
        ProjectInstance wikiPi = wikiPump.mineData(pump.getEntityManager());
        wikiPump.close();

        for (Person person : wikiPi.getProject().getPeople()) {
            for (Identity identity : person.getIdentities()) {
                addPerson(identity);
            }
        }

        String commitUrlPrefix = wikiPi.getUrl().substring(0, wikiPi.getUrl().lastIndexOf(App.GIT_SUFFIX)) + GitHubPump.COMMIT_URL_SUFFIX;
        Map<String, Artifact> artifactMap = new HashMap<>();

        for (Commit commit : wikiPi.getProject().getCommits()) {

            Configuration configuration = new Configuration();
            configuration.setExternalId(commit.getExternalId());
            configuration.setName(commit.getName());
            configuration.setDescription(commit.getDescription());
            configuration.setCreated(commit.getCreated());
            configuration.setUrl(commitUrlPrefix + commit.getName());
            for (Identity identity : configuration.getAuthor().getIdentities()) {
                configuration.setAuthor(addPerson(identity));
            }

            for (WorkItemChange wikiChange : configuration.getChanges()) {
                WorkItemChange change = new WorkItemChange();
                change.setExternalId(wikiChange.getExternalId());
                change.setType(wikiChange.getType());
                change.setDescription(wikiChange.getDescription());

                if (wikiChange.getType().equals(WorkItemChange.Type.MODIFY)) {
                    FieldChange fieldChange = new FieldChange();
                    fieldChange.setName(DESC_FIELD_NAME);
                    change.getFieldChanges().add(fieldChange);
                }
                // TODO try mining content and its changes
                Artifact wikiPage = (Artifact) wikiChange.getChangedItem();
                if (!artifactMap.containsKey(wikiPage.getName())) {
                    Artifact page = new Artifact();
                    page.setExternalId(wikiPage.getExternalId());
                    page.setName(wikiPage.getName());
                    page.setUrl(wikiPage.getUrl());
                    page.setDescription(wikiPage.getDescription());
                    page.setCreated(wikiPage.getCreated());
                    page.setArtifactClass(ArtifactClass.WIKIPAGE);
                    for (Identity identity : wikiPage.getAuthor().getIdentities()) {
                        page.setAuthor(addPerson(identity));
                    }
                    artifactMap.put(page.getName(), page);
                }
                change.setChangedItem(artifactMap.get(wikiPage.getName()));

                configuration.getChanges().add(change);
            }
        }
    }
}
