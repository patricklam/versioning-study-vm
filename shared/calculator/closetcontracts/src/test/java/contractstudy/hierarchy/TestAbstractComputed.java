package contractstudy.hierarchy;

import contractstudy.ContractElement;
import contractstudy.ConstraintCollector;
import contractstudy.ConstraintType;
import contractstudy.scripts.CollectContracts;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static test.contractstudy.TestJsr303Annotations.firstByKind;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class TestAbstractComputed {

    public static final File APP_DATA_FOLDER = new File("src/test/java/contractstudy/hierarchy/testdata/abstr/");

    @Test
    public void testAbstractMethod() throws Exception {

        ConstraintCollector collector = new ConstraintCollector();
        CollectContracts.analyse(new File(APP_DATA_FOLDER, "AbstractClassMethods.java"), collector);

        ContractElement min = firstByKind(collector.getContractElements(), ConstraintType.JSR303Min);
        assertEquals("method()", min.getMethodDeclaration());
        assertFalse(min.isMethodAbstract());

        ContractElement max = firstByKind(collector.getContractElements(), ConstraintType.JSR303Max);
        assertEquals("abstrMethod()", max.getMethodDeclaration());
        assertTrue(max.isMethodAbstract());
    }

    @Test
    public void testDefaultMethod() throws Exception {

        ConstraintCollector collector = new ConstraintCollector();
        CollectContracts.analyse(new File(APP_DATA_FOLDER, "DefaultInterface.java"), collector);

        ContractElement min = firstByKind(collector.getContractElements(), ConstraintType.JSR303Min);
        assertEquals("defMethod()", min.getMethodDeclaration());
        assertFalse(min.isMethodAbstract());

        ContractElement max = firstByKind(collector.getContractElements(), ConstraintType.JSR303Max);
        assertEquals("interMethod()", max.getMethodDeclaration());
        assertTrue(max.isMethodAbstract());
    }

}
