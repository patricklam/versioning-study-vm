package semverstudy.closetcontracts;

import contractstudy.Location;
import semverstudy.commons.Project;
import semverstudy.commons.ProjectVersion;

/**
 * Abstraction for how to process results.
 * @author jens dietrich
 */
public interface ResultListener {

    void resultFound(Project project, ProjectVersion projectVersion1, ProjectVersion projectVersion2, Location location, String violationType, String detail);
}
