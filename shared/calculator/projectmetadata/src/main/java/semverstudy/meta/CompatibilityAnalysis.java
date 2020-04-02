package semverstudy.meta;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import semverstudy.commons.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Compatibility analysis for project version metadata (poms and similar).
 * @author jens dietrich
 */
public class CompatibilityAnalysis {

    public static final String TOOL_NAME = "metadata-analyser";
    public static final String LICENSE_ADDED = "LICENSE_ADDED";
    public static final String LICENSE_REMOVED = "LICENSE_REMOVED";
    public static final String DEPENDENCY_VERSION_CHANGED = "DEPENDENCY_VERSION_CHANGED";
    public static final String DEPENDENCY_ADDED = "DEPENDENCY_ADDED";
    public static final String DEPENDENCY_REMOVED = "DEPENDENCY_ADDED";
    public static final String MISSING_INFORMATION = "n/a";

    public static final MetaDataExtractor[] EXTRACTORS = {
            new POMMetaDataExtractor()
    };


    public static final Logger LOGGER = Logging.getLogger("metadata-analysis");

    public static void main (String[] args) throws Exception {

        Preconditions.checkArgument(args.length == 2, "two argument is required - the json file describing projects to be analysed and the output file (in this order)");
        File projectSpecs = new File(args[0]);
        Preconditions.checkArgument(projectSpecs.exists(), "file containing project specs does not exist: " + projectSpecs.getAbsolutePath());

        Project[] projects = ProjectParser.parseProjects(projectSpecs);
        LOGGER.info("" + projects.length + " projects found");

        File output = new File(args[1]);
        List<DiffResult> results = new ArrayList<>();


        ResultListener reporter = new ResultListener() {
            DiffResult currentResult = null;
            @Override
            public void resultFound(Project project, ProjectVersion projectVersion1, ProjectVersion projectVersion2, String metaDataLocation1,String metaDataLocation2, String issue, String detail) {
                LOGGER.info("Incompatible change for in project " + project.getName());
                LOGGER.info("\tversion 1: " + projectVersion1.getVersion());
                LOGGER.info("\tversion 2: " + projectVersion2.getVersion());
                LOGGER.info("\tlocation 1: " + metaDataLocation1);
                LOGGER.info("\tlocation 2: " + metaDataLocation2);
                LOGGER.info("\tviolation: " + issue);
                LOGGER.info("\tdetail: " + detail);

                if (currentResult==null || !currentResult.getName().equals(project.getName()) || !currentResult.getVersion1().equals(projectVersion1.getVersion()) || !currentResult.getVersion2().equals(projectVersion2.getVersion())) {
                    currentResult = new DiffResult();
                    currentResult.setName(project.getName());
                    currentResult.setVersion1(projectVersion1.getVersion());
                    currentResult.setVersion2(projectVersion2.getVersion());
                    results.add(currentResult);
                }

                DiffIssue diffIssue = new DiffIssue();
                diffIssue.setTool(TOOL_NAME);
                diffIssue.setKey(issue);
                diffIssue.setFile(metaDataLocation2);
                diffIssue.setDirection("+1"); // TODO need some clarification here from Patrick
                currentResult.getIssues().add(diffIssue);

            }
        };
        analyse(projects, reporter);
        DiffResultExporter.writeResults(results,output);
        LOGGER.info("Results written to " + output.getAbsolutePath());
    }

    // public to make this testable
    public static void analyse(Project[] projects, ResultListener reporter) throws Exception {

        int total = (int)Stream.of(projects).flatMap(p -> Stream.of(p.getVersions())).count();
        AtomicInteger progressCounter = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();
        for (Project project:projects) {

            LOGGER.info("Analysing project " + project.getName());
            MetaData metaDataOfPreviousVersion = null;
            ProjectVersion previousVersion = null;

            // NOTE: VERSIONS ARE PROCESSED AND DIFFED IN THE ORDER AS DEFINED IN THE JSON FILE
            for (ProjectVersion projectVersion : project.getVersions()) {
                LOGGER.info("Analysing project version " + project.getName() + " - "+ projectVersion.getVersion());
                MetaData metaData = null;
                // chain of responsibility
                for (MetaDataExtractor extractor:EXTRACTORS) {
                    if (metaData==null) {
                        metaData = extractor.extractMetaData(project,projectVersion);
                    }
                }
                diffAndReport(project,previousVersion,projectVersion,metaDataOfPreviousVersion,metaData,reporter);
                previousVersion = projectVersion;
                metaDataOfPreviousVersion = metaData;
            }
        }

        long endTime = System.currentTimeMillis();

        LOGGER.info("Analysis finished");
        LOGGER.info("\ttime: " + (endTime-startTime) + " ms");
    }

    private static void diffAndReport(Project project, ProjectVersion projectVersion1, ProjectVersion projectVersion2, MetaData metaData1, MetaData metaData2,ResultListener reporter) {
        checkForChangedLicenses(project,projectVersion1,projectVersion2,metaData1,metaData2,reporter);
        checkForChangedDependencies(project,projectVersion1,projectVersion2,metaData1,metaData2,reporter);
    }

    private static void checkForChangedDependencies(Project project, ProjectVersion projectVersion1, ProjectVersion projectVersion2, MetaData metaData1, MetaData metaData2, ResultListener reporter) {
        Set<License> dependencies1 = metaData1.getLicenses();
        Set<License> dependencies2 = metaData2.getLicenses();
    }

    private static void checkForChangedLicenses(Project project, ProjectVersion projectVersion1, ProjectVersion projectVersion2, MetaData metaData1, MetaData metaData2, ResultListener reporter) {
        Set<License> licenses1 = metaData1.getLicenses();
        Set<License> licenses2 = metaData2.getLicenses();

        // check for added licenses
        for (License license:licenses2) {
            if (!licenses1.stream().anyMatch(l -> license.sameAs(l))) {
                reporter.resultFound(project,projectVersion1,projectVersion2,metaData1.getLocation(),metaData2.getLocation(),LICENSE_ADDED,toString(license));
            }
        }
        // report removed licenses
        for (License license:licenses1) {
            if (!licenses2.stream().anyMatch(l -> license.sameAs(l))) {
                reporter.resultFound(project,projectVersion1,projectVersion2,metaData1.getLocation(),metaData2.getLocation(),LICENSE_REMOVED,toString(license));
            }
        }
    }

    private static String toString(License license) {
        assert license.getName()!=null || license.getUrl()!=null;
        String s = license.getName();
        if (s==null) {
            s = ""+license.getUrl();
        }
        return s;
    }

    private static void reportChange(Project project, ProjectVersion projectVersion1, ProjectVersion projectVersion2, String location, String violationType, String detail, ResultListener reporter) {
        System.out.println("Incompatible change for in project " + project);
        System.out.println("\tversion 1: " + projectVersion1.getVersion());
        System.out.println("\tversion 2: " + projectVersion2.getVersion());
        System.out.println("\tlocation: " + location);
        System.out.println("\tviolation: " + violationType);
        System.out.println("\tdetail: " + detail);
    }

}
