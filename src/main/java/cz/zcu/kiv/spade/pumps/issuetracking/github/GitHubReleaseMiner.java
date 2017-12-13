package cz.zcu.kiv.spade.pumps.issuetracking.github;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.Commit;
import cz.zcu.kiv.spade.domain.VCSTag;
import cz.zcu.kiv.spade.pumps.ReleaseMiner;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTag;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GitHubReleaseMiner extends ReleaseMiner {

    private static final String RELEASES_MINED_FORMAT = "mined %d/%d releases (/%d)";
    private static final int RELEASES_BATCH_SIZE = 100;

    GitHubReleaseMiner(GitHubPump pump) {
        super(pump);
    }

    @Override
    public void mineTags() {

        List<GHRelease> releases;
        List<GHTag> tags;
        while (true) {
            try {
                releases = ((GHRepository) pump.getRootObject()).listReleases().asList();
                tags = ((GHRepository) pump.getRootObject()).listTags().asList();
                break;
            } catch (IOException e) {
                ((GitHubPump) pump).resetRootObject();
            }
        }

        Map<String, VCSTag> spadeTags = new HashMap<>();
        for (Commit commit : pump.getPi().getProject().getCommits()) {
            for (VCSTag tag : commit.getTags()) {
                if (!spadeTags.containsKey(tag.getName())) {
                    spadeTags.put(tag.getName(), tag);
                }
            }
        }

        int i = 0;
        for (GHRelease release : releases) {
            VCSTag spadeTag = spadeTags.get(release.getTagName());
            if (release.getName() != null) {
                spadeTag.setDescription(release.getName());
            }
            if (release.getBody() != null) {
                spadeTag.setDescription(spadeTag.getDescription() + "\n" + release.getBody().trim());
            }
            i++;
            if ((i % RELEASES_BATCH_SIZE) == 0) {
                ((GitHubPump) pump).checkRateLimit();
            }
        }
        App.printLogMsg(String.format(RELEASES_MINED_FORMAT, i, releases.size(), tags.size()));
    }
}
