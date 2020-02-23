package contractstudy;

import contractstudy.diffrules.Utils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class ConditionAddedTest {


    @Test
    public void testSimpleConditionAnd() {
        String c1 = "a > b";
        String c2 = "a   >  b  && c == 0";

        assertTrue("Condition added", conditionStrengthen(c1, c2));
    }

    @Test
    public void testSimpleConditionOr() {
        String c1 = "a > b";
        String c2 = "a   >  b  || c == 0";

        assertTrue("Condition added", conditionWeaken(c1, c2));
    }

    @Test
    public void testConditionSwap() {
        String c1 = "a > b";
        String c2 = "c == 0 && a   >  b";

        assertTrue("Condition added", conditionStrengthen(c1, c2));
    }

    @Test
    public void testConditionCombination() {
        String c1 = "a > b";
        String c2 = "c == 0 && a > b || c < 1";

        assertFalse("Condition incompatible", conditionWeaken(c1, c2));
        assertFalse("Condition incompatible", conditionStrengthen(c1, c2));
    }

    @Test
    public void testConditionSame() {
        String c1 = "a > b";
        String c2 = "a   >  b";

        assertTrue("Condition same", conditionStrengthen(c1, c2));
    }

    @Test
    public void testConditionCombinedRules() {
        String c1 = "a > b  &&          v == 0  || x > 2  ";
        String c2 = "a > b || x > 2 &&  v == 0  || xx > 2 ";

        assertTrue("Condition added", conditionWeaken(c1, c2));
    }

    @Test
    public void testConditionIncompatible() {
        String c1 = "a > b && v == 0";
        String c2 = "a > b || v == 0 || xx > 2 ";

        assertFalse("Condition incompatible", conditionStrengthen(c1, c2));
    }

    @Test
    public void testNoCondition() {
        String c1 = "ahoj";
        String c2 = "ciao";

        assertFalse("Condition incompatible", conditionWeaken(c1, c2));
        assertFalse("Condition incompatible", conditionStrengthen(c1, c2));
    }

    private static boolean conditionStrengthen(String c1, String c2) {
        return Utils.constraintStrengthened(wrap(c1), wrap(c2));
    }

    private static boolean conditionWeaken(String c1, String c2) {
        return Utils.constraintWeakened(wrap(c1), wrap(c2));
    }


    private static ContractElement wrap(String condition) {
        ContractElement c = new ContractElement();
        c.setCondition(condition);

        return c;
    }

}
