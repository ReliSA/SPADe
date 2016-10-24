package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.enums.ArtifactClass;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Entity
public class Artifact extends WorkItem {

    private ArtifactClass artifactClass;
    private String mimeType;
    private long size;

    public Artifact() {
    }

    public Artifact(long id, String externalId, String name, String description, Date created, Person author, String url,
                    ArtifactClass artifactClass, String mimeType, long size) {
        super(id, externalId, name, description, created, author, url);
        this.artifactClass = artifactClass;
        this.mimeType = mimeType;
        this.size = size;
    }

    @Enumerated(value = EnumType.STRING)
    public ArtifactClass getArtifactClass() {
        return artifactClass;
    }

    public void setArtifactClass(ArtifactClass artifactClass) {
        this.artifactClass = artifactClass;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
