package semverstudy.meta;

import java.util.Objects;

/**
 * Representation of a Maven-style dependency.
 * @author jens dietrich
 */
public class Dependency {

    private String groupId = null;
    private String artifactId = null;
    private String version = null;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    // same group and artifact id, perhaps different version
    public boolean sameArtifactAs(Dependency other) {
        if (other==null) return false;
        return Objects.equals(this.getGroupId(),other.getGroupId()) && Objects.equals(this.getArtifactId(),other.getArtifactId());
    }

    // note handling of variables -- always assume it is different !
    public boolean sameVersionAs(Dependency other) {
        if (!this.sameArtifactAs(other)) return false;
        if (this.versionIsVariable()) return false;
        if (other.versionIsVariable()) return false;
        return Objects.equals(this.getVersion(),other.getVersion());
    }

    // TODO: check whether this is Maven specific, if so must make this a field
    public boolean versionIsVariable() {
        return this.version != null && this.version.startsWith("${");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependency that = (Dependency) o;
        return Objects.equals(groupId, that.groupId) &&
                Objects.equals(artifactId, that.artifactId) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version);
    }


    @Override
    public String toString() {
        return "Dependency{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
