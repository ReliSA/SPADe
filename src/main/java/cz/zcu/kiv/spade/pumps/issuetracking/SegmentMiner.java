package cz.zcu.kiv.spade.pumps.issuetracking;

import cz.zcu.kiv.spade.domain.Activity;
import cz.zcu.kiv.spade.domain.Iteration;
import cz.zcu.kiv.spade.domain.Phase;
import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;
import cz.zcu.kiv.spade.pumps.DataMiner;
import cz.zcu.kiv.spade.pumps.DataPump;

import java.util.Collection;

public abstract class SegmentMiner extends DataMiner {

    protected SegmentMiner(DataPump pump) {
        super(pump);
    }

    /**
     * collects all iterations in the project
     * and assigns the proper ones to the Work Units
     */
    protected void mineIterations() {
        Collection<ProjectSegment> iterations = collectIterations();
        for (WorkUnit unit : pump.getPi().getProject().getUnits()) {
            for (ProjectSegment iteration : iterations) {
                if (unit.getIteration() != null &&
                        unit.getIteration().getExternalId().equals(iteration.getExternalId())) {
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

    protected void duplicate(Collection<ProjectSegment> iterations, Iteration iteration) {
        Phase phase = new Phase();
        phase.setProject(iteration.getProject());
        phase.setExternalId(iteration.getExternalId());
        phase.setName(iteration.getName());
        phase.setDescription(iteration.getDescription());
        phase.setEndDate(iteration.getEndDate());

        Activity activity = new Activity();
        activity.setProject(iteration.getProject());
        activity.setExternalId(iteration.getExternalId());
        activity.setName(iteration.getName());
        activity.setDescription(iteration.getDescription());
        activity.setEndDate(iteration.getEndDate());

        iterations.add(phase);
        iterations.add(activity);
    }

    /**
     * collects all iterations (milestones, phases, etc.) in the project and saves each one as Iteration, Phase and Activity
     * for future analysis
     *
     * @return all project segments in all 3 forms
     */
    protected abstract Collection<ProjectSegment> collectIterations();
}
