package contractstudy.hierarchy;

import contractstudy.ProgramVersion;
import contractstudy.hierarchy.testdata.depsa.SimpleParentParentClassA;
import contractstudy.hierarchy.testdata.depsa.SimpleParentParentInterfaceA;
import contractstudy.hierarchy.testdata.depsb.SimpleParentParentB;
import contractstudy.hierarchy.testdata.simple.SimpleParentInterface;
import contractstudy.hierarchy.testdata.simple.SimpleSubClass;
import contractstudy.hierarchy.testdata.simple.another.SimpleParentClass;
import contractstudy.hierarchy.testdata.simple.multiple.MultipleA;
import contractstudy.hierarchy.testdata.simple.multiple.MultipleB;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class TestHierarchy {

    public static final File APP_DATA_FOLDER = new File("src/test/java/contractstudy/hierarchy/testdata/simple/");

    public static final File TEST_DEPSA_DATA_FOLDER = new File("src/test/java/contractstudy/hierarchy/testdata/depsa/");

    public static final File TEST_DEPSB_DATA_FOLDER = new File("src/test/java/contractstudy/hierarchy/testdata/depsb/");

    private ProjectVersionHierarchyExtractor extractor = new ProjectVersionHierarchyExtractor();

    @Test
    public void testClassAndInterface() throws Exception {

        List<String> result = new ArrayList<>();
        ProgramVersion app = ProgramVersion.getOrCreate("app", "1.0").withFile(APP_DATA_FOLDER);
        ProgramVersion depa = ProgramVersion.getOrCreate("depa", "1.0").withFile(TEST_DEPSA_DATA_FOLDER);
        ProgramVersion depb = ProgramVersion.getOrCreate("depb", "1.0").withFile(TEST_DEPSB_DATA_FOLDER);

        Map<String, ClassCoordinates> allClasses = new HashMap<>();
        extractor.analyse(app, Arrays.asList(depa, depb), new InheritanceResolved() {
            @Override
            public void notify(ClassParents classParents) {
                Set<ClassAndVersion> parents = classParents.getParents(classParents.getClassName());
                allClasses.put(classParents.getClassName(), classParents);
                for (ClassAndVersion parent : parents) {
                    result.add(TestHierarchy.toString(parent));
                }
                // structure of inner classes is flat for now
                for (ClassCoordinates innerClass : classParents.getInnerClasses()) {
                    Set<ClassAndVersion> innerParents = classParents.getParents(innerClass.getClassName());
                    allClasses.put(innerClass.getClassName(), innerClass);
                    for (ClassAndVersion parent : innerParents) {
                        result.add(TestHierarchy.toString(parent));
                    }
                }
            }

            @Override
            public void notify(ClassCoordinates classCoordinates) {

            }
        });

        String fileName = APP_DATA_FOLDER.getPath();
        assertThat(result, hasItem("app-1.0:" + fileName + ":" + SimpleParentClass.class.getName()));
        assertThat(result, hasItem("app-1.0:" + fileName + ":" + SimpleParentInterface.class.getName()));
        assertThat(result, hasItem("app-1.0:" + fileName + ":" + MultipleA.class.getName()));
        assertThat(result, hasItem("app-1.0:" + fileName + ":" + MultipleB.class.getName()));

        fileName = TEST_DEPSA_DATA_FOLDER.getPath();
        // grand parents
        assertThat(result, hasItem("depa-1.0:" + fileName + ":" + SimpleParentParentClassA.class.getName()));
        assertThat(result, hasItem("depa-1.0:" + fileName + ":" + SimpleParentParentInterfaceA.class.getName()));

        fileName = TEST_DEPSB_DATA_FOLDER.getPath();
        // grand parents
        assertThat(result, hasItem("depb-1.0:" + fileName + ":" + SimpleParentParentB.class.getName()));

        // check methods loaded.
        Set<String> parentMethods = allClasses.get(SimpleParentClass.class.getName()).getMethods();
        assertThat(parentMethods, hasItem("publicM()"));

        Set<String> interMethods = allClasses.get(SimpleParentInterface.class.getName()).getMethods();
        // we ignore interface methods right now
        // assertThat(interMethods, hasItem("method()"));

        Set<String> multipleAMethods = allClasses.get(MultipleA.class.getName()).getMethods();
        // we ignore interface methods right now
//        assertThat(multipleAMethods, IsIterableContainingInAnyOrder.containsInAnyOrder("method1()", "method2()"));

        Set<String> subclassMethods = allClasses.get(SimpleSubClass.class.getName()).getMethods();
        assertThat(subclassMethods, IsIterableContainingInAnyOrder.containsInAnyOrder("method()", "method1()", "method2()", "methodParentParent()"));

    }

    private static String toString(ClassAndVersion classAndVersion) {
        return classAndVersion.getProgramVersion().getName()
                + "-" + classAndVersion.getProgramVersion().getVersion()
                + ":" + classAndVersion.getProgramVersion().getFile()
                + ":" + classAndVersion.getClassName();
    }

}
