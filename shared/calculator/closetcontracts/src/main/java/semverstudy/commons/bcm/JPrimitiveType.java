package semverstudy.commons.bcm;

import java.util.Collection;
import java.util.Objects;

/**
 * Primitive type representation in a simplified bytecode model.
 * @author jens dietrich
 */
public class JPrimitiveType extends JType {
    private String name = null;

    public JPrimitiveType(String name) {
        this.name = name;
    }

    public static final JPrimitiveType INT = new JPrimitiveType(Integer.TYPE.getSimpleName());
    public static final JPrimitiveType BOOLEAN = new JPrimitiveType(Boolean.TYPE.getSimpleName());
    public static final JPrimitiveType CHAR = new JPrimitiveType(Character.TYPE.getSimpleName());
    public static final JPrimitiveType BYTE = new JPrimitiveType(Byte.TYPE.getSimpleName());
    public static final JPrimitiveType SHORT = new JPrimitiveType(Short.TYPE.getSimpleName());
    public static final JPrimitiveType FLOAT = new JPrimitiveType(Float.TYPE.getSimpleName());
    public static final JPrimitiveType LONG = new JPrimitiveType(Long.TYPE.getSimpleName());
    public static final JPrimitiveType DOUBLE = new JPrimitiveType(Double.TYPE.getSimpleName());
    public static final JPrimitiveType VOID = new JPrimitiveType(Void.TYPE.getSimpleName());

    @Override
    public String toString() {
        return "JPrimitiveType{" + name + '}';
    }

    @Override
    public String getName() {
        return this.name;
    }
}
