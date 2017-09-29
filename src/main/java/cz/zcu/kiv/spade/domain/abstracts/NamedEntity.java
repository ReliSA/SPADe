package cz.zcu.kiv.spade.domain.abstracts;

import com.vdurmont.emoji.EmojiParser;
import cz.zcu.kiv.spade.App;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.HashSet;
import java.util.Set;

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
            name = cleanString(name);
            if (name.length() > 255) {
                name = name.substring(0, 252) + "...";
            }
            this.name = name;
        }
    }

    @Transient
    String cleanString(String text) {
        text = EmojiParser.parseToAliases(text);
        Set<String> codePoints = new HashSet<>();
        for (int offset = 0; offset < text.length();) {
            int charCount = Character.charCount(text.codePointAt(offset));
            if (charCount > 1) {
                String sub = text.substring(offset, offset + charCount);
                codePoints.add(sub);
            }
            offset += charCount;
        }
        for (String codePoint : codePoints) {
            App.log.println(codePoint);
            text = text.replaceAll(codePoint, "");
        }
        return text;
    }
}
