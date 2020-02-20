package contractstudy.hierarchy;

import contractstudy.ProgramVersion;
import contractstudy.hierarchy.testdata.innerclassdeps.TestInterfaceInner;
import contractstudy.hierarchy.testdata.innerclassdeps.TestInterfaceOuter;
import contractstudy.hierarchy.testdata.innerclasses.ClassWithInner;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;


/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class TestHierarchyInnerClasses {

    public static final File APP_DATA_FOLDER = new File("src/test/java/contractstudy/hierarchy/testdata/innerclasses/");
    public static final File LIB_DATA_FOLDER = new File("src/test/java/contractstudy/hierarchy/testdata/innerclassdeps/");


    private ProjectVersionHierarchyExtractor extractor = new ProjectVersionHierarchyExtractor();

    @Test
    public void testClassAndInterface() throws Exception {

        Set<String> result = new HashSet<>();
        ProgramVersion app = ProgramVersion.getOrCreate("app", "1.0").withFile(APP_DATA_FOLDER);
        ProgramVersion dep = ProgramVersion.getOrCreate("dep", "1.0").withFile(LIB_DATA_FOLDER);

        Map<String, ClassCoordinates> allClasses = new HashMap<>();
        extractor.analyse(app, Collections.singletonList(dep), new InheritanceResolved() {
            @Override
            public void notify(ClassParents classParents) {
                Set<ClassAndVersion> parents = classParents.getParents(classParents.getClassName());
                allClasses.put(classParents.getClassName(), classParents);
                for (ClassAndVersion parent : parents) {
                    result.add(TestHierarchyInnerClasses.toString(classParents, parent));
                }
                // structure of inner classes is flat for now
                for (ClassCoordinates innerClass : classParents.getInnerClasses()) {
                    Set<ClassAndVersion> innerParents = classParents.getParents(innerClass.getClassName());
                    allClasses.put(classParents.getClassName(), classParents);
                    for (ClassAndVersion parent : innerParents) {
                        result.add(TestHierarchyInnerClasses.toString(innerClass, parent));
                    }
                }
            }

            @Override
            public void notify(ClassCoordinates classCoordinates) {
                allClasses.put(classCoordinates.getClassName(), classCoordinates);
                for (ClassCoordinates inner : classCoordinates.getInnerClasses())  {
                    allClasses.put(inner.getClassName(), inner);
                }
            }
        });


        assertThat(result, hasItem(c(ClassWithInner.class) + "->" + c(TestInterfaceOuter.class)));
        assertThat(result, hasItem(c(ClassWithInner.InnerInherit.class) + "->" + c(TestInterfaceInner.class)));
        assertThat(result, hasItem(c(ClassWithInner.InnerStaticInherit.class) + "->" + c(TestInterfaceInner.class)));

        Set<String> topLevelMethods = allClasses.get(ClassWithInner.class.getName()).getMethods();
        assertThat(topLevelMethods, hasItem("method()"));
        assertThat(topLevelMethods, hasItem("lastMethod()"));

        Set<String> innerMethods = allClasses.get(c(ClassWithInner.InnerNoInherit.class)).getMethods();
        assertThat(innerMethods, hasItem("methodInner()"));
    }


    private static String c(Class clazz) {
        return clazz.getName().replace("$", ".");
    }

    private static String toString(
            final ClassCoordinates subClass,
            final ClassAndVersion parentClass) {

        return subClass.getClassName() + "->" + parentClass.getClassName();
    }

}
