package contractstudy.hierarchy.testdata.abstr;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public abstract class AbstractClassMethods {

    @Max(30)
    public abstract void abstrMethod();

    @Min(40)
    public void method() {

    }
}
