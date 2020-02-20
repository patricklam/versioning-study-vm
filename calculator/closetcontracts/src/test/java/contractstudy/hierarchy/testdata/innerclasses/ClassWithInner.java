package contractstudy.hierarchy.testdata.innerclasses;

import contractstudy.hierarchy.testdata.innerclassdeps.TestInterfaceInner;
import contractstudy.hierarchy.testdata.innerclassdeps.TestInterfaceOuter;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class ClassWithInner implements TestInterfaceOuter {

    // some elements
    Integer field;

    public void method() {}

    public class InnerNoInherit {

        // comment
        Integer fieldInner;

        public void methodInner() {}
    }

    public static class  InnerStaticNoInherit {

        interface InnerInnerInterf {

        }

    }

    public class InnerInherit implements TestInterfaceInner {

        // commetn
        Number fieldInner2;

        void m() {}

    }

    public static class InnerStaticInherit implements TestInterfaceInner {
    }



    public void lastMethod() {

    }

}
