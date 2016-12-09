package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "keyword")
public class EnumKeyword extends DescribedEntity {

    private List<FieldAndClass> fieldsAndClasses;

    public EnumKeyword() {
        super();
        this.fieldsAndClasses = new ArrayList<>();
    }

    @ManyToMany
    @JoinTable(name = "keyword_field_and_class", joinColumns = @JoinColumn(name = "keywordId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "fieldAndClassId", referencedColumnName = "id"))
    public List<FieldAndClass> getFieldsAndClasses() {
        return fieldsAndClasses;
    }

    public void setFieldsAndClasses(List<FieldAndClass> fieldsAndClasses) {
        this.fieldsAndClasses = fieldsAndClasses;
    }
}
