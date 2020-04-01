package semverstudy.commons.bcm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Class representation in a simplified bytecode model.
 * @author jens dietrich
 */
public class JClass extends JType {
    String name = null;
    Collection<JMethod> methods = new HashSet<>();
    Collection<JField> fields = new HashSet<>();

    public JClass(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Collection<JMethod> getMethods() {
        return methods;
    }

    public Collection<JField> getFields() {
        return fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JClass jClass = (JClass) o;
        return Objects.equals(name, jClass.name) && Objects.equals(methods, jClass.methods) && Objects.equals(fields, jClass.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, methods, fields);
    }

    @Override
    public String toString() {
        return "JClass{" + "name='" + name + '\'' + '}';
    }
}
