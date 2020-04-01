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
