package semverstudy.meta;

import semverstudy.commons.Project;
import semverstudy.commons.ProjectVersion;

/**
 * Interface for utilities to extract meta data.
 * @author jens dietrich
 */
public interface MetaDataExtractor {

    MetaData extractMetaData(Project project, ProjectVersion version) throws Exception;
}
