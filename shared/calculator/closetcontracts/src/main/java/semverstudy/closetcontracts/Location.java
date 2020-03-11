package semverstudy.closetcontracts;

import contractstudy.ContractElement;

import java.util.Objects;

/**
 * Describes a location where an issue was detected.
 * @author jens dietrich
 */
public class Location {

    private String cu = null;
    private String methodDeclaration = null;
    private int lineNo = -1;

    public static Location newFrom(ContractElement ce) {
        return new Location(ce.getCuName(),ce.getMethodDeclaration(),ce.getLineNo());
    }

    public Location(String cu, String methodDeclaration, int lineNo) {
        this.cu = cu;
        this.methodDeclaration = methodDeclaration;
        this.lineNo = lineNo;
    }

    public int getLineNo() {
        return lineNo;
    }

    public String getCu() {
        return cu;
    }

    public String getMethodDeclaration() {
        return methodDeclaration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(cu, location.cu) && Objects.equals(methodDeclaration, location.methodDeclaration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cu, methodDeclaration);
    }
}
