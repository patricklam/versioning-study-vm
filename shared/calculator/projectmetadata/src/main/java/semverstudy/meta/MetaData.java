package semverstudy.meta;

import java.util.Objects;
import java.util.Set;

/**
 * Representation of Maven meta data.
 * @author jens dietrich
 */
public class MetaData {

    private Set<Dependency> dependencies = null;
    private Set<License> licenses = null;
    private MetaDataKind kind = null;
    private String location = null; // POM or similar

    public MetaDataKind getKind() {
        return kind;
    }

    public void setKind(MetaDataKind kind) {
        this.kind = kind;
    }

    public Set<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public Set<License> getLicenses() {
        return licenses;
    }

    public void setLicenses(Set<License> licenses) {
        this.licenses = licenses;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "MetaData{" + location + '}';
    }

    // note that location is not included -- e.g. a projectmay switch from mvn to gradle, as long
    // as only the metadata representation but not the actual metadata have changed, this is ok

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaData metaData = (MetaData) o;
        return Objects.equals(dependencies, metaData.dependencies) &&
                Objects.equals(licenses, metaData.licenses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependencies, licenses);
    }
}
