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

    public ToolInstance(long id, long externalId, Tool tool, String version) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ToolInstance toolInstance = (ToolInstance) o;

        if (tool != toolInstance.tool) return false;
        return !(version != null ? !version.equals(toolInstance.version) : toolInstance.version != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (tool != null ? tool.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ToolInstance{" +
                "tool=" + tool +
                ", version='" + version + '\'' +
                '}';
    }
}
