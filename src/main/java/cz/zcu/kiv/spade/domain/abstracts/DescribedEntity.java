package cz.zcu.kiv.spade.domain.abstracts;

import com.vdurmont.emoji.EmojiParser;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.nio.charset.Charset;

@MappedSuperclass
public abstract class DescribedEntity extends NamedEntity {

    protected String description = "";

    public DescribedEntity() {
        super();
    }

    @Column(columnDefinition = "longtext")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null) {
            this.description = EmojiParser.parseToAliases(description);
        }
    }

}
