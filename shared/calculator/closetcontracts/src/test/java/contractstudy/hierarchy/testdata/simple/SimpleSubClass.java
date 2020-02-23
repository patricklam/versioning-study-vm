package contractstudy.hierarchy.testdata.simple;

import contractstudy.hierarchy.testdata.simple.another.SimpleParentClass;

import contractstudy.hierarchy.testdata.simple.multiple.*;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class SimpleSubClass extends SimpleParentClass
        implements SimpleParentInterface, MultipleA, MultipleB {


    @Override
    public void method1() {

    }

    @Override
    public void method2() {

    }

    @Override
    public void method() {

    }

    @Override
    public void methodParentParent() {

    }
}
