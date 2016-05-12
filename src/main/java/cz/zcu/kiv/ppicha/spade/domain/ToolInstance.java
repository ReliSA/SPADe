package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.BaseEntity;
import cz.zcu.kiv.ppicha.spade.domain.enums.Tool;

import javax.persistence.*;

@Entity
public class ToolInstance extends BaseEntity {

    private Tool tool;
    private String version;

    public ToolInstance() {
    }

    public ToolInstance(long id, String externalId, Tool tool, String version) {
        super(id, externalId);
        this.tool = tool;
        this.version = version;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    public Tool getTool() {
        return tool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }

    @Column(nullable = false)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
