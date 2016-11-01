package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;
import cz.zcu.kiv.spade.domain.enums.Tool;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

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
    public Tool getTool() {
        return tool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return super.toString() +
                "Tool: " + tool.name() + "\n" +
                "Version: " + version + "\n";
    }
}
