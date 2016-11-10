package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;

@Entity
@Table(name = "project_instance")
public class ProjectInstance extends DescribedEntity {

    private ToolInstance toolInstance;
    private Project project;
    private String url;

    public ProjectInstance() {
        super();
    }

    @JoinColumn(name = "toolInstanceId")
    @ManyToOne(fetch = FetchType.LAZY)
    public ToolInstance getToolInstance() {
        return toolInstance;
    }

    public void setToolInstance(ToolInstance toolInstance) {
        this.toolInstance = toolInstance;
    }

    @JoinColumn(name = "projectId")
    @ManyToOne(fetch = FetchType.LAZY)
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
