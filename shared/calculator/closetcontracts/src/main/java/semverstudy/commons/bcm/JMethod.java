package semverstudy.commons.bcm;

import java.util.List;
import java.util.Objects;

/**
 * Method representation in a simplified bytecode model.
 * @author jens dietrich
 */
public class JMethod {
    String name = null;
    String descriptor = null;
    JType returnType = null;
    List<JType> paramTypes = null;

    public JMethod(String name, String descriptor, JType returnType, List<JType> paramTypes) {
        this.name = name;
        this.descriptor = descriptor;
        this.returnType = returnType;
        this.paramTypes = paramTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JMethod jMethod = (JMethod) o;
        return Objects.equals(name, jMethod.name) && Objects.equals(descriptor, jMethod.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, descriptor);
    }

    @Override
    public String toString() {
        return "JMethod{" + name + descriptor + '}';
    }
}
