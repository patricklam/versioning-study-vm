package semverstudy.commons.bcm;

/**
 * Array representation in a simplified bytecode model.
 * @author jens dietrich
 */

public class JArray extends JType {
    JType componentType = null;

    public JArray(JType componentType) {
        this.componentType = componentType;
    }

    public JType getComponentType() {
        return componentType;
    }

    public void setComponentType(JType componentType) {
        this.componentType = componentType;
    }

    @Override
    public String toString() {
        return "JArray{" + "componentType=" + componentType + '}';
    }

    @Override
    public String getName() {
        return this.componentType.getName() + "[]";
    }
}
