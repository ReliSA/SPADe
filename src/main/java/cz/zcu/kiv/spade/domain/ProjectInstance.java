package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.Set;

@Entity
public class ProjectInstance extends DescribedEntity {

    private ToolInstance toolInstance;
    private Project project;
    private String url;

    public ProjectInstance() {
    }

    public ProjectInstance(long id, String externalId, String name, String description, ToolInstance toolInstance, Project project, String url) {
        super(id, externalId, name, description);
        this.toolInstance = toolInstance;
        this.project = project;
        this.url = url;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public ToolInstance getToolInstance() {
        return toolInstance;
    }

    public void setToolInstance(ToolInstance toolInstance) {
        this.toolInstance = toolInstance;
    }

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

    @Override
    public String toString() {
        return super.toString() +
                "URL: " + url + "\n" +
                "Tool Instance:\n" +
                "<----------------------------------------\n" +
                toolInstance.toString() +
                ">----------------------------------------\n" +
                "Project:\n" +
                "<----------------------------------------\n" +
                project.toString() +
                ">----------------------------------------\n";
    }
}
