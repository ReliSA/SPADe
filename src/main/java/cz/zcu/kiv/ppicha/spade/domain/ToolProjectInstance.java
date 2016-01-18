package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.NamedEntity;

import javax.persistence.*;

@Entity
public class ToolProjectInstance extends NamedEntity {

    private ToolInstance toolInstance;
    private String url;
    private Project project;

    public ToolProjectInstance() {
    }

    public ToolProjectInstance(long id, long externalId, String name, ToolInstance toolInstance, String url, Project project) {
        super(id, externalId, name);
        this.toolInstance = toolInstance;
        this.url = url;
        this.project = project;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public ToolInstance getToolInstance() {
        return toolInstance;
    }

    public void setToolInstance(ToolInstance toolInstance) {
        this.toolInstance = toolInstance;
    }

    @Column(nullable = false)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ToolProjectInstance that = (ToolProjectInstance) o;

        if (toolInstance != null ? !toolInstance.equals(that.toolInstance) : that.toolInstance != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        return !(project != null ? !project.equals(that.project) : that.project != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (toolInstance != null ? toolInstance.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (project != null ? project.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ToolProjectInstance{" +
                "toolInstance=" + toolInstance +
                ", url='" + url + '\'' +
                ", project=" + project +
                '}';
    }
}
