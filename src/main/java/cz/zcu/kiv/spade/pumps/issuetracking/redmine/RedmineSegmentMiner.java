package cz.zcu.kiv.spade.pumps.issuetracking.redmine;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Version;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.Iteration;
import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;
import cz.zcu.kiv.spade.pumps.issuetracking.SegmentMiner;

import java.util.Collection;
import java.util.LinkedHashSet;

class RedmineSegmentMiner extends SegmentMiner {

    private static final String VERSIONS_PERMISSION_ERR_MSG = "Insufficient permissions for versions";

    RedmineSegmentMiner(RedminePump pump) {
        super(pump);
    }

    @Override
    protected Collection<ProjectSegment> collectIterations() {
        Collection<ProjectSegment> iterations = new LinkedHashSet<>();
        try {
            for (Version version : ((RedmineManager) pump.getRootObject()).getProjectManager().getVersions(((Project) pump.getSecondaryObject()).getId())) {
                Iteration iteration = new Iteration();
                iteration.setProject(pump.getPi().getProject());
                iteration.setExternalId(version.getId().toString());
                iteration.setName(version.getName());
                iteration.setDescription(version.getDescription());
                iteration.setCreated(version.getCreatedOn());
                iteration.setStartDate(version.getCreatedOn());
                iteration.setEndDate(version.getDueDate());

                iterations.add(iteration);
                duplicate(iterations, iteration);
            }
        } catch (RedmineException e) {
            App.printLogMsg(this, VERSIONS_PERMISSION_ERR_MSG);
        }
        return iterations;
    }
}
