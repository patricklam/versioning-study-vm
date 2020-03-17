package semverstudy.commons.bcm;

import java.util.Objects;

/**
 * Field representation in a simplified bytecode model.
 * @author jens dietrich
 */
class JField {
    String name = null;
    String descriptor = null;
    JType type = null;

    public JField(String name, String descriptor, JType type) {
        this.name = name;
        this.descriptor = descriptor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JField jField = (JField) o;
        return Objects.equals(name, jField.name) &&
                Objects.equals(descriptor, jField.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, descriptor);
    }

    @Override
    public String toString() {
        return "JField{" +
                "name='" + name + '\'' +
                '}';
    }
}
