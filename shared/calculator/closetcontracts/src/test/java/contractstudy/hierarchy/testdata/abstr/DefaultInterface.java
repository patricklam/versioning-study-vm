package contractstudy.hierarchy.testdata.abstr;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public interface DefaultInterface {


    @Max(30)
    void interMethod();

    @Min(20)
    default void defMethod() {

    }
}
