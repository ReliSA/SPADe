package cz.zcu.kiv.spade.pumps.issuetracking.bugzilla;

import b4j.core.Project;
import b4j.core.Version;
import cz.zcu.kiv.spade.domain.Iteration;
import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;
import cz.zcu.kiv.spade.pumps.issuetracking.SegmentMiner;

import java.util.Collection;
import java.util.LinkedHashSet;

class BugzillaSegmentMiner extends SegmentMiner {

    BugzillaSegmentMiner(BugzillaPump pump) {
        super(pump);
    }

    @Override
    protected Collection<ProjectSegment> collectIterations() {
        Collection<ProjectSegment> iterations = new LinkedHashSet<>();
        for (Version version : ((Project) pump.getSecondaryObject()).getVersions()) {
            Iteration iteration = new Iteration();
            iteration.setProject(pump.getPi().getProject());
            iteration.setExternalId(version.getId().toString());
            iteration.setName(version.getName());
            if (version.getReleaseDate() != null) {
                iteration.setEndDate(version.getReleaseDate());
            }

            iterations.add(iteration);
            duplicate(iterations, iteration);
        }
        return iterations;
    }
}
