package cz.zcu.kiv.spade.domain.abstracts;

import javax.persistence.MappedSuperclass;
import java.nio.charset.Charset;

@MappedSuperclass
public abstract class NamedEntity extends ExternalEntity {

    protected String name = "";

    public NamedEntity() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            byte[] bytes = name.getBytes();
            name = new String(bytes, Charset.forName("UTF-8"));
            if (name.length() > 255) {
                name = name.substring(0, 252) + "...";
            }
            this.name = name;
        }
    }

}
