
// class that has a method with one constraint in v1, and no contraint in v2

import javax.validation.constraints.Min;

public class OneToZero {
    @Notnull
    public void foo(@Min(42) int i) {
        System.out.println(i);
    }
}