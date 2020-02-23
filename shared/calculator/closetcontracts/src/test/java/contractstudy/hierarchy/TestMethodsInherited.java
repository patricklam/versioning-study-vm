package contractstudy.hierarchy;

import com.google.common.collect.HashMultimap;
import contractstudy.Preferences;
import contractstudy.hierarchy.testdata.simple.SimpleParentInterface;
import contractstudy.hierarchy.testdata.simple.SimpleSubClass;
import contractstudy.hierarchy.testdata.simple.another.SimpleParentClass;
import contractstudy.maven.MavenProjectVersion;
import contractstudy.scripts.ComputeInheritanceHierarchy;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static contractstudy.SubtypeDiffExtractor.readInheritanceCSV;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

/**
 * Check that methods are loaded and correctly propagated in inheritance hierarchies.
 *
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class TestMethodsInherited {

    private final HashMultimap<ClassAndVersion, ClassAndVersion> inheritanceMap = HashMultimap.create();
    private final Map<ClassAndVersion, Set<String>> methods = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        // compute data
        Preferences.setSinglePref("output", "src/test/resources");
        Preferences.setSinglePref("data", "src/test/java/contractstudy/hierarchy/testdata/");
        mockInputData();
        ComputeInheritanceHierarchy.main(new String[]{"--skip-jdk"});

        // load data
        readInheritanceCSV(inheritanceMap, methods);
    }

    @Test
    public void testMethodDirectlyPropagated() {

        ClassAndVersion subClass = classInst(SimpleSubClass.class);

        // this class implements all
        Set<String> subClassMethods = methods.get(subClass);
        assertThat(subClassMethods, hasItem("method()"));
        assertThat(subClassMethods, hasItem("method1()"));
        assertThat(subClassMethods, hasItem("method2()"));
        assertThat(subClassMethods, hasItem("methodParentParent()"));
    }

    @Test
    public void testMethodSkipped() {

        ClassAndVersion subClass = classInst(SimpleParentInterface.class);

        // this class implements all
        Set<String> subClassMethods = methods.get(subClass);
        assertThat(subClassMethods, not(hasItem("method()")));
        assertThat(subClassMethods, not(hasItem("parentParentMethod()"))); // propagated from super
    }

    @Test
    public void testMethodPropagatedSuper() {

        ClassAndVersion subClass = classInst(SimpleParentClass.class);

        // this class implements all
        Set<String> subClassMethods = methods.get(subClass);
        assertThat(subClassMethods, hasItem("publicM()"));
        assertThat(subClassMethods, hasItem("parentParentInProjectMethod()")); // propagated from super
        assertThat(subClassMethods, not(hasItem("parentParentMethod()"))); // in dependencies - ignored now
    }


    private static ClassAndVersion classInst(Class className) {
        String fqn = className.getName().replace("contractstudy.hierarchy.testdata.simple.", "");  // strip beginning
        String cuName = fqn.replace('.', '/') + ".java";
        return ClassAndVersion.create("simple", "1.0", "", cuName);
    }


    private void mockInputData() throws IOException {
        MavenProjectVersion projectVersion = new MavenProjectVersion("simple", "simple", "1.0");
        File jsonFile = new File(new File(Preferences.getDataFolder(), "simple"), "simple-1.0-deps.json");
        IOUtils.write(projectVersion.toJson(), new FileOutputStream(jsonFile), "utf-8");

        File root = new File(Preferences.getDataFolder(), "simple");
        compressZipfile(
                root.getPath() + File.separator,
                new File(root, "simple-1.0.zip").getPath() + File.separator);
    }

    public static void compressZipfile(String sourceDir, String outputFile) throws IOException {
        ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(outputFile));
        compressDirectoryToZipfile(sourceDir, sourceDir, zipFile);
        IOUtils.closeQuietly(zipFile);
    }

    private static void compressDirectoryToZipfile(String rootDir, String sourceDir, ZipOutputStream out) throws IOException, FileNotFoundException {
        for (File file : new File(sourceDir).listFiles()) {
            if (file.isDirectory()) {
                compressDirectoryToZipfile(rootDir, sourceDir + file.getName() + File.separator, out);
            } else {
                ZipEntry entry = new ZipEntry(sourceDir.replace(rootDir, "") + file.getName());
                out.putNextEntry(entry);

                FileInputStream in = new FileInputStream(sourceDir + file.getName());
                IOUtils.copy(in, out);
                IOUtils.closeQuietly(in);
            }
        }
    }
}
