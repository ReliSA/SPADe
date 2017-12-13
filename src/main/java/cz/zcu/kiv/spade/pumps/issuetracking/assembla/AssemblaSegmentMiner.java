package cz.zcu.kiv.spade.pumps.issuetracking.assembla;

import com.assembla.Milestone;
import com.assembla.client.AssemblaAPI;
import cz.zcu.kiv.spade.domain.Iteration;
import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;
import cz.zcu.kiv.spade.pumps.issuetracking.SegmentMiner;

import java.util.Collection;
import java.util.LinkedHashSet;

class AssemblaSegmentMiner extends SegmentMiner {

    private static final String MILESTONE_DESC_FORMAT = "%s\n\nRelease Note:\n%s\n\nBudget: %s";

    AssemblaSegmentMiner(AssemblaPump pump) {
        super(pump);
    }

    @Override
    protected Collection<ProjectSegment> collectIterations() {
        Collection<ProjectSegment> iterations = new LinkedHashSet<>();
        for (Milestone milestone : ((AssemblaAPI) pump.getRootObject()).milestones(pump.getPi().getExternalId()).getAll().asList()) {
            Iteration iteration = new Iteration();
            iteration.setProject(pump.getPi().getProject());
            iteration.setExternalId(milestone.getId());
            iteration.setName(milestone.getTitle());
            if (milestone.getDueDate() != null) {
                iteration.setEndDate(((AssemblaPump) pump).convertDate(milestone.getDueDate()));
            }
            iteration.setDescription(String.format(MILESTONE_DESC_FORMAT, milestone.getDescription(), milestone.getReleaseNotes(), milestone.getBudget()));
            iteration.setCreated(((AssemblaPump) pump).convertDate(milestone.getCreatedAt()));
            iteration.setStartDate(iteration.getCreated());

            iterations.add(iteration);
            duplicate(iterations, iteration);
        }
        return iterations;
    }
}
