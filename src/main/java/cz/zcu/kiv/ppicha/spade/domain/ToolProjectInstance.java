package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;

@Entity
public class ToolProjectInstance extends DescribedEntity {

    private ToolInstance toolInstance;
    private String url;

    public ToolProjectInstance() {
    }

    public ToolProjectInstance(long id, String externalId, String name, String description, ToolInstance toolInstance, String url, Project project) {
        super(id, externalId, name, description);
        this.toolInstance = toolInstance;
        this.url = url;
    }

    @ManyToOne(fetch = FetchType.LAZY)
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

}
