package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rest.client.domain.Version;
import cz.zcu.kiv.spade.domain.Iteration;
import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;
import cz.zcu.kiv.spade.pumps.issuetracking.SegmentMiner;

import java.util.Collection;
import java.util.LinkedHashSet;

class JiraSegmentMiner extends SegmentMiner {

    JiraSegmentMiner(JiraPump pump) {
        super(pump);
    }

    @Override
    protected Collection<ProjectSegment> collectIterations() {
        Collection<ProjectSegment> iterations = new LinkedHashSet<>();
        for (Version version : ((Project) pump.getSecondaryObject()).getVersions()) {
            Iteration iteration = new Iteration();
            iteration.setProject(pump.getPi().getProject());
            if (version.getId() != null) {
                iteration.setExternalId(version.getId().toString());
            }
            iteration.setName(version.getName());
            iteration.setDescription(version.getDescription());
            if (version.getReleaseDate() != null) {
                iteration.setEndDate(version.getReleaseDate().toDate());
            }

            iterations.add(iteration);
            duplicate(iterations, iteration);
        }
        return iterations;
    }
}
