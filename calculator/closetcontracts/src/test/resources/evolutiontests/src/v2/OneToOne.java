
// class that has a method with one constraint in v1, and another contraint in v2

import javax.validation.constraints.Max;

public class OneToZero {
    @Notnull
    public void foo(@Max(42) int i) {
        System.out.println(i);
    }
}