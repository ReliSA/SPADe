package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DefinedProjectSegment;

import javax.persistence.*;

@Entity
@Table(name = "phase")
public class Phase extends DefinedProjectSegment {

    private Milestone milestone;

    public Phase() {
        super();
    }

    @JoinColumn(name = "milestoneId")
    @OneToOne(fetch = FetchType.LAZY)
    public Milestone getMilestone() {
        return milestone;
    }

    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
    }

}
