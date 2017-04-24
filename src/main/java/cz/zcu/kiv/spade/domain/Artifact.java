package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.enums.ArtifactClass;

import javax.persistence.*;

@Entity
@Table(name = "artifact")
@DiscriminatorValue("ARTIFACT")
public class Artifact extends WorkItem {

    private ArtifactClass artifactClass;
    private String mimeType;
    private long size;

    public Artifact() {
        super();
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
