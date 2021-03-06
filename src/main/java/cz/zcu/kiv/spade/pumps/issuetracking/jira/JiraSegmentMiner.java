package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rest.client.domain.Version;
import cz.zcu.kiv.spade.domain.Activity;
import cz.zcu.kiv.spade.domain.Iteration;
import cz.zcu.kiv.spade.domain.Phase;
import cz.zcu.kiv.spade.domain.WorkUnit;
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

    @Override
    protected void mineIterations() {
        Collection<ProjectSegment> iterations = collectIterations();
        for (WorkUnit unit : pump.getPi().getProject().getUnits()) {
            for (ProjectSegment iteration : iterations) {
                if (unit.getIteration() != null &&
                        unit.getIteration().getName().equals(iteration.getName())) {
                    if (iteration instanceof Iteration) {
                        Iteration i = (Iteration) iteration;
                        unit.setIteration(i);
                        if (unit.getDueDate() == null) {
                            unit.setDueDate(iteration.getEndDate());
                        }
                    }
                    if (iteration instanceof Phase) {
                        Phase phase = (Phase) iteration;
                        unit.setPhase(phase);
                    }
                    if (iteration instanceof Activity) {
                        Activity activity = (Activity) iteration;
                        unit.setActivity(activity);
                    }
                }
            }
        }
    }
}
