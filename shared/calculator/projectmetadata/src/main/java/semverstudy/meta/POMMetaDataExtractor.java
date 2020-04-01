package semverstudy.meta;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import semverstudy.commoms.Logging;
import semverstudy.commoms.Project;
import semverstudy.commoms.ProjectParser;
import semverstudy.commoms.ProjectVersion;

import java.io.File;

/**
 * Metadata extractor for pom.xml files, using the structure described http://maven.apache.org/pom.html.
 * @author jens dietrich
 */
public class POMMetaDataExtractor implements MetaDataExtractor {

    public static final Logger LOGGER = Logging.getLogger("pom-metadata-extraction");


    // for testing only !
    public static void main (String[] args) throws Exception {
        File projectSpecs = new File(args[0]);
        Project[] projects = ProjectParser.parseProjects(projectSpecs);
        ProjectVersion someVersion = projects[0].getVersions()[0];
        new POMMetaDataExtractor().extractMetaData(someVersion);

    }

    @Override
    public MetaData extractMetaData(ProjectVersion projectVersion) throws Exception {
        LOGGER.info("extracting metadata from " + projectVersion);
        return null;
    }



}
