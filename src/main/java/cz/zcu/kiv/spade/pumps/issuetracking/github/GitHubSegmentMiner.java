package cz.zcu.kiv.spade.pumps.issuetracking.github;

import cz.zcu.kiv.spade.domain.Iteration;
import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;
import cz.zcu.kiv.spade.pumps.issuetracking.SegmentMiner;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHMilestone;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

class GitHubSegmentMiner extends SegmentMiner {

    private static final int SEGMENT_BATCH_SIZE = 100;

    GitHubSegmentMiner(GitHubPump pump) {
        super(pump);
    }

    @Override
    protected Collection<ProjectSegment> collectIterations() {
        Collection<ProjectSegment> iterations = new LinkedHashSet<>();
        int i = 1;
        List<GHMilestone> milestones = ((GHRepository) pump.getRootObject()).listMilestones(GHIssueState.ALL).asList();
        for (GHMilestone milestone : milestones) {
            Iteration iteration = new Iteration();
            iteration.setProject(pump.getPi().getProject());
            iteration.setExternalId(Long.toString(milestone.getId()));
            iteration.setName(milestone.getTitle());
            if (milestone.getDescription() != null) {
                iteration.setDescription(milestone.getDescription().trim());
            }

            Date creation;
            while (true) {
                try {
                    creation = milestone.getCreatedAt();
                    break;
                } catch (IOException e) {
                    ((GitHubPump) pump).resetRootObject();
                }
            }
            iteration.setCreated(creation);
            iteration.setStartDate(iteration.getCreated());
            iteration.setEndDate(milestone.getDueOn());

            iterations.add(iteration);
            duplicate(iterations, iteration);

            if ((i % SEGMENT_BATCH_SIZE) == 0) {
                ((GitHubPump) pump).checkRateLimit();
            }
            i++;
        }

        return iterations;
    }
}
