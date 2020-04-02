package semverstudy.meta;

import semverstudy.commons.Project;
import semverstudy.commons.ProjectVersion;

/**
 * Abstraction for how to process results.
 * @author jens dietrich
 */
public interface ResultListener {

    void resultFound(Project project, ProjectVersion projectVersion1, ProjectVersion projectVersion2, String metaDataLocation1,String metaDataLocation2,String violationType, String detail);
}
