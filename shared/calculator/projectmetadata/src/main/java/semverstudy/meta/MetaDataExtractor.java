package semverstudy.meta;

import semverstudy.commoms.ProjectVersion;

/**
 * Interface for utilities to extract meta data.
 * @author jens dietrich
 */
public interface MetaDataExtractor {

    MetaData extractMetaData(ProjectVersion version) throws Exception;
}
