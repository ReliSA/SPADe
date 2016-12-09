package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.EnumClass;
import cz.zcu.kiv.spade.domain.enums.EnumField;
import cz.zcu.kiv.spade.domain.enums.EnumSuperClass;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "project_instance")
public class ProjectInstance extends DescribedEntity {

    private ToolInstance toolInstance;
    private Project project;
    private String url;
    private Collection<EnumKeyword> keywords;

    public ProjectInstance() {
        super();
        this.keywords = new LinkedHashSet<>();
        setDefaultKeywords();
    }

    @JoinColumn(name = "toolInstanceId")
    @ManyToOne(fetch = FetchType.LAZY)
    public ToolInstance getToolInstance() {
        return toolInstance;
    }

    public void setToolInstance(ToolInstance toolInstance) {
        this.toolInstance = toolInstance;
    }

    @JoinColumn(name = "projectId")
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

    @ManyToMany
    @JoinTable(name = "project_instance_keyword", joinColumns = @JoinColumn(name = "projectInstanceId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "keywordId", referencedColumnName = "id"))
    public Collection<EnumKeyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(Collection<EnumKeyword> keywords) {
        this.keywords = keywords;
    }

    @Transient
    public Map<String, Map<EnumField, EnumClass>> getKeywordsMap() {
        Map<String, Map<EnumField, EnumClass>> keywordsMap = new HashMap<>();
        for (EnumKeyword enumKeyword : keywords) {
            keywordsMap.put(enumKeyword.getName(), new HashMap<>());
            for (FieldAndClass fac : enumKeyword.getFieldsAndClasses()) {
                keywordsMap.get(enumKeyword.getName()).put(fac.getEnumField(), fac.getEnumClass());
            }
        }
        return keywordsMap;
    }

    @Transient
    public EnumSuperClass getSuperClass(String keyword, EnumField field) {
        EnumClass enumClass = getEnumClass(keyword, field);

        if (field.equals(EnumField.RESOLUTION)) {

            if (enumClass.equals(EnumClass.INCOMPLETE) || enumClass.equals(EnumClass.WORKS_FOR_ME))
                return EnumSuperClass.UNFINISHED;
            else
                return EnumSuperClass.FINISHED;

        } else if (field.equals(EnumField.SEVERITY)) {

            if (enumClass.equals(EnumClass.MINOR) || enumClass.equals(EnumClass.TRIVIAL))
                return EnumSuperClass.MINOR;
            else if (enumClass.equals(EnumClass.CRITICAL) || enumClass.equals(EnumClass.MAJOR))
                return EnumSuperClass.MAJOR;
            else
                return EnumSuperClass.NORMAL;

        } else if (field.equals(EnumField.PRIORITY)) {

            if (enumClass.equals(EnumClass.LOW) || enumClass.equals(EnumClass.LOWEST))
                return EnumSuperClass.LOW;
            else if (enumClass.equals(EnumClass.HIGH) || enumClass.equals(EnumClass.HIGHEST))
                return EnumSuperClass.HIGH;
            else
                return EnumSuperClass.NORMAL;

        } else if (field.equals(EnumField.STATUS)) {

            if (enumClass.equals(EnumClass.DONE) || enumClass.equals(EnumClass.INVALID))
                return EnumSuperClass.CLOSED;
            else
                return EnumSuperClass.OPEN;

        } else return null;
    }

    @Transient
    public EnumClass getEnumClass(String keyword, EnumField field) {
        if (!getKeywordsMap().isEmpty()
                && getKeywordsMap().containsKey(keyword)
                && getKeywordsMap().get(keyword).containsKey(field))
            return getKeywordsMap().get(keyword).get(field);
        else return null;
    }

    @Transient
    public void addKeyword(EnumField field, EnumClass enumClass, String keyword) {
        boolean found = false;
        for (EnumKeyword enumKeyword : keywords) {
            if (enumKeyword.equals(field)) {
                enumKeyword.getFieldsAndClasses().add(new FieldAndClass(field, enumClass));
                found = true;
                break;
            }
        }
        if (!found) {
            EnumKeyword enumKeyword = new EnumKeyword();
            enumKeyword.setName(keyword);
            List<FieldAndClass> facs = new ArrayList<>();
            facs.add(new FieldAndClass(field, enumClass));
            enumKeyword.setFieldsAndClasses(facs);
        }
    }

    @Transient
    private void setDefaultKeywords() {
        addKeyword(EnumField.TYPE, EnumClass.BUG, "bug");
        addKeyword(EnumField.TYPE, EnumClass.BUG, "defect");
        addKeyword(EnumField.TYPE, EnumClass.TASK, "task");
        addKeyword(EnumField.TYPE, EnumClass.ENHANCEMENT, "enhancement");
        addKeyword(EnumField.TYPE, EnumClass.ENHANCEMENT, "improvement");
        addKeyword(EnumField.TYPE, EnumClass.FEATURE, "feature");
        addKeyword(EnumField.TYPE, EnumClass.FEATURE, "newfeature");

        addKeyword(EnumField.RESOLUTION, EnumClass.INVALID, "invalid");
        addKeyword(EnumField.RESOLUTION, EnumClass.DUPLICATE, "duplicate");
        addKeyword(EnumField.RESOLUTION, EnumClass.WONT_FIX, "wontfix");
        addKeyword(EnumField.RESOLUTION, EnumClass.WONT_FIX, "wontdo");
        addKeyword(EnumField.RESOLUTION, EnumClass.FIXED, "fixed");
        addKeyword(EnumField.RESOLUTION, EnumClass.FIXED, "done");
        addKeyword(EnumField.RESOLUTION, EnumClass.FIXED, "fixedupstream");
        addKeyword(EnumField.RESOLUTION, EnumClass.WORKS_AS_DESIGNED, "worksasdesigned");
        addKeyword(EnumField.RESOLUTION, EnumClass.WORKS_FOR_ME, "worksforme");
        addKeyword(EnumField.RESOLUTION, EnumClass.INCOMPLETE, "incomplete");
        addKeyword(EnumField.RESOLUTION, EnumClass.INCOMPLETE, "cannotreproduce");

        addKeyword(EnumField.SEVERITY, EnumClass.TRIVIAL, "trivial");
        addKeyword(EnumField.SEVERITY, EnumClass.MINOR, "minor");
        addKeyword(EnumField.SEVERITY, EnumClass.MINOR, "small");
        addKeyword(EnumField.SEVERITY, EnumClass.NORMAL, "normal");
        addKeyword(EnumField.SEVERITY, EnumClass.NORMAL, "moderate");
        addKeyword(EnumField.SEVERITY, EnumClass.NORMAL, "common");
        addKeyword(EnumField.SEVERITY, EnumClass.MAJOR, "major");
        addKeyword(EnumField.SEVERITY, EnumClass.MAJOR, "big");
        addKeyword(EnumField.SEVERITY, EnumClass.CRITICAL, "critical");
        addKeyword(EnumField.SEVERITY, EnumClass.CRITICAL, "blocker");

        addKeyword(EnumField.PRIORITY, EnumClass.LOWEST, "lowest");
        addKeyword(EnumField.PRIORITY, EnumClass.LOW, "low");
        addKeyword(EnumField.PRIORITY, EnumClass.NORMAL, "normal");
        addKeyword(EnumField.PRIORITY, EnumClass.NORMAL, "medium");
        addKeyword(EnumField.PRIORITY, EnumClass.HIGH, "high");
        addKeyword(EnumField.PRIORITY, EnumClass.HIGHEST, "highest");
        addKeyword(EnumField.PRIORITY, EnumClass.HIGHEST, "immediate");
        addKeyword(EnumField.PRIORITY, EnumClass.HIGHEST, "urgent");

        addKeyword(EnumField.STATUS, EnumClass.NEW, "new");
        addKeyword(EnumField.STATUS, EnumClass.NEW, "todo");
        addKeyword(EnumField.STATUS, EnumClass.NEW, "unconfirmed");
        addKeyword(EnumField.STATUS, EnumClass.NEW, "funnel");
        addKeyword(EnumField.STATUS, EnumClass.NEW, "analysis");
        addKeyword(EnumField.STATUS, EnumClass.NEW, "open");
        addKeyword(EnumField.STATUS, EnumClass.ACCEPTED, "accepted");
        addKeyword(EnumField.STATUS, EnumClass.ACCEPTED, "assigned");
        addKeyword(EnumField.STATUS, EnumClass.ACCEPTED, "backlog");
        addKeyword(EnumField.STATUS, EnumClass.IN_PROGRESS, "inprogress");
        addKeyword(EnumField.STATUS, EnumClass.RESOLVED, "resolved");
        addKeyword(EnumField.STATUS, EnumClass.RESOLVED, "test");
        addKeyword(EnumField.STATUS, EnumClass.VERIFIED, "verified");
        addKeyword(EnumField.STATUS, EnumClass.VERIFIED, "feedback");
        addKeyword(EnumField.STATUS, EnumClass.DONE, "done");
        addKeyword(EnumField.STATUS, EnumClass.DONE, "closed");
        addKeyword(EnumField.STATUS, EnumClass.DONE, "fixed");
        addKeyword(EnumField.STATUS, EnumClass.DONE, "approved");
        addKeyword(EnumField.STATUS, EnumClass.INVALID, "invalid");
        addKeyword(EnumField.STATUS, EnumClass.INVALID, "cancelled");
        addKeyword(EnumField.STATUS, EnumClass.INVALID, "rejected");
    }
}
