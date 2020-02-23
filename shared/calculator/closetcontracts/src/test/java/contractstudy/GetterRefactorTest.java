package contractstudy;

import contractstudy.diffrules.Utils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class GetterRefactorTest {

    @Test
    public void testToGetter() {
        String c1 = "foo";
        String c2 = "getFoo()";

        assertTrue(conditionSame(c1, c2));
    }

    @Test
    public void testToIs() {
        String c1 = "foo";
        String c2 = "isFoo()";

        assertTrue(conditionSame(c1, c2));
    }

    @Test
    public void testCombined() {
        String c1 = "foo = 10 && boo == ahoj";
        String c2 = "getFoo() = 10 && getBoo() == ahoj";

        assertTrue(conditionSame(c1, c2));
    }


    private static boolean conditionSame(String c1, String c2) {
        return Utils.getterRefactor(wrap(c1), wrap(c2));
    }

    private static ContractElement wrap(String condition) {
        ContractElement c = new ContractElement();
        c.setCondition(condition);

        return c;
    }
}
