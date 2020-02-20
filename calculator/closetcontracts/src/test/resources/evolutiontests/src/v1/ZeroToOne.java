
// class that has a method with no constraint in v1, and one contraint in v2

import javax.validation.constraints.Min;

public class ZeroToOne {
    @Notnull
    public void foo(int i) {
        System.out.println(i);
    }
}