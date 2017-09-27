package cz.zcu.kiv.spade.domain.abstracts;

import com.vdurmont.emoji.EmojiParser;
import org.mozilla.universalchardet.UniversalDetector;

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
            name = EmojiParser.parseToAliases(name);
            if (name.length() > 255) {
                name = name.substring(0, 252) + "...";
            }
            this.name = name;
        }
    }
}
