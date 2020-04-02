package semverstudy.meta;

import semverstudy.commons.Project;
import semverstudy.commons.ProjectVersion;
import javax.annotation.Nullable;

/**
 * Interface for utilities to extract meta data.
 * @author jens dietrich
 */
public interface MetaDataExtractor {

    /**
     * Extracts and returns metadata. null is returned to signal that no metadata can be extracted, this can be used to
     * build chains of responsibility.
     * @param project
     * @param version
     * @return
     * @throws Exception
     */
    @Nullable
    MetaData extractMetaData(Project project, ProjectVersion version) throws Exception;
}
