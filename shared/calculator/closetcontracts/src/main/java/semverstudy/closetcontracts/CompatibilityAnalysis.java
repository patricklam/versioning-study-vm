package semverstudy.closetcontracts;

import com.google.common.base.Preconditions;
import contractstudy.*;
import contractstudy.extractors.*;
import org.apache.log4j.Logger;
import semverstudy.commons.*;
import semverstudy.commons.Downloader;
import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Compatibility analysis for project versions, as defined in the jspn spec.
 * @author jens dietrich
 */
public class CompatibilityAnalysis {

    public static final Extractor[] EXTRACTORS = {
            new JSR303Extractor(),
            new JSR305Extractor(),
            new JSR305ConcurrentExtractor(),
            new FindBugsAnnotationExtractor(),
            new JetBrainsExtractor(),
            new IntellijExtractor(),
            new JavaAssertExtractor(),
            new ConditionalRuntimeExceptionExtractor(),
            new UnconditionalOperationNotSupportedExceptionExtractor(),
            new GuavaPreconditionsExtractor(),
            new CommonsValidate2Extractor(),
            new CommonsValidate3Extractor(),
            new SpringAssertExtractor()
    };

    public static final Logger LOGGER = Logging.getLogger("closetcontract-analysis");

    public static void main (String[] args) throws Exception {

        Preconditions.checkArgument(args.length==1,"one argument is required - the json file describing projects to be analysed");
        File projectSpecs = new File(args[0]);
        Preconditions.checkArgument(projectSpecs.exists(),"file containing project specs does not exist: " + projectSpecs.getAbsolutePath());

        Project[] projects = ProjectParser.parseProjects(projectSpecs);
        LOGGER.info(""+projects.length + " projects found");

        int total = (int)Stream.of(projects).flatMap(p -> Stream.of(p.getVersions())).count();
        AtomicInteger progressCounter = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();

        for (Project project:projects) {
            LOGGER.info("Analysing project " + project.getName());
            AtomicInteger constraintCounter = new AtomicInteger(0);
            AtomicInteger parsedCUCounter = new AtomicInteger(0);
            AtomicInteger parserFailedCUCounter = new AtomicInteger(0);
            AtomicInteger parsedProgramVersionCounter = new AtomicInteger(0);
            List<ContractElement> contractsOfPreviousVersion = null;
            ProjectVersion previousVersion = null;
            // NOTE: VERSIONS ARE PROCESSED AND DIFFED IN THE ORDER AS DEFINED IN THE JSON FILE
            for (ProjectVersion projectVersion : project.getVersions()) {
                parsedProgramVersionCounter.incrementAndGet();
                LOGGER.info("Analysing project version " + project.getName() + " - "+ projectVersion.getVersion());
                File projectVersionFolder = Downloader.download(projectVersion.getSource());
                ConstraintCollector collector = new ConstraintCollector() {
                    @Override
                    public void extractionExceptionEncountered(String message, Throwable x) {
                        super.extractionExceptionEncountered(message, x);
                        parserFailedCUCounter.incrementAndGet();
                    }
                };
                findContractElementsinProject(projectVersionFolder, collector, project.getName(), "foo-version", parsedCUCounter);
                System.out.println("Processed " + progressCounter.incrementAndGet() + "/" + total + ": " + projectVersionFolder.getAbsolutePath() + " -- " + collector.getContractElements().size() + " contracts found");
                constraintCounter.addAndGet(collector.getContractElements().size());

                List<ContractElement> contracts = collector.getContractElements();
                if (contractsOfPreviousVersion!=null) {
                    diffAndReport(project,projectVersion,previousVersion,contracts,contractsOfPreviousVersion);
                }
                previousVersion = projectVersion;
                contractsOfPreviousVersion = contracts;
            }
        }

        long endTime = System.currentTimeMillis();

        LOGGER.info("Done!");
        LOGGER.info("\ttime: " + (endTime-startTime) + " ms");
//        LOGGER.info("\tchecked CUs: " + parsedCUCounter.intValue());
//        LOGGER.info("\tCUs where parsing failed: " + parserFailedCUCounter.intValue());
//        LOGGER.info("\tprogram versions checked: " + parsedProgramVersionCounter.intValue());
//        LOGGER.info("\tcontracts found: " + constraintCounter.intValue());
    }

    private static void diffAndReport(Project project, ProjectVersion projectVersion, ProjectVersion previousVersion, List<ContractElement> contracts, List<ContractElement> contractsOfPreviousVersion) {
        LOGGER.warn("Not yet implemented: diffing contracts between version " + projectVersion.getVersion() + " and " + previousVersion.getVersion() );
    }


    @SuppressWarnings("unchecked")
    private static void findContractElementsinProject(File project, ExtractionListener<ContractElement> consumer, String programName, String version, AtomicInteger parsedCUCounter) throws Exception {

        FileVisitor<Path> visitor = new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (ProjectTraversalFilters.IS_PROGRAM_SOURCE.test(file)) {
                    findContractElementInSrcFile(file,consumer, programName, version, parsedCUCounter);
                }
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        };

        Files.walkFileTree(project.toPath(), visitor);
    }

    private static void findContractElementInSrcFile(Path javaSrc, ExtractionListener<ContractElement> consumer, String programName, String version, AtomicInteger parsedCUCounter) {

        String cuName = ProjectTraversalFilters.getProgramCUName(javaSrc);
        for (Extractor<ContractElement> extractor:EXTRACTORS) {
            try (FileInputStream in = new FileInputStream(javaSrc.toFile())) {
                extractor.analyse(in, programName, version, cuName, consumer);
            }
            catch (Exception x) {
                x.printStackTrace();
            }
        }
    }

}
