package semverstudy.commons.bcm;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Simple descriptor parser.
 * @autjor jens dietrich
 */
public class DescriptorParser {
    /**
     * Parses a descriptor and returns a pair consisting of parameter and return types.
     */
    public static Pair<List<JType>, JType> parseMethodDescriptor(String descr, Function<String, JType> typeFactory)  {
        ImmutableList<Character> chars = Lists.charactersOf(descr);
        Iterator<Character> iter = chars.listIterator();

        Preconditions.checkArgument(!descr.isEmpty()); // descriptors must not be empty
        assert iter.hasNext();
        char next = iter.next();
        Preconditions.checkArgument(next=='('); // descriptors start with (

        List<JType> paramTypes = new ArrayList<>();
        JType returnType = null;

        JType nextType = null;
        while ((nextType = parseType(descr,iter,typeFactory))!=null) {
            paramTypes.add(nextType);
        }

        nextType = parseType(descr,iter,typeFactory);
        if (nextType!=null) {
            returnType = nextType;
        }

        return Pair.of(paramTypes,returnType);
    }

    /**
     * Parses a single type name.
     */
    public static JType parseFieldDescriptor(String descr, Function<String, JType> typeFactory)  {
        if (descr.length()==1) {
            char c = descr.charAt(0);
            switch (c) {
                case 'I': return JPrimitiveType.INT;
                case 'Z': return JPrimitiveType.BOOLEAN;
                case 'C': return JPrimitiveType.CHAR;
                case 'B': return JPrimitiveType.BYTE;
                case 'S': return JPrimitiveType.SHORT;
                case 'F': return JPrimitiveType.FLOAT;
                case 'J': return JPrimitiveType.LONG;
                case 'D': return JPrimitiveType.DOUBLE;
                case 'V': return JPrimitiveType.VOID;
            }
        }
        else if (descr.startsWith("L") && descr.endsWith(";")) {
            String name = descr.substring(1,descr.length()-1);
            name = name.replaceAll("/",".");
            return typeFactory.apply(name);
        }
        else if (descr.startsWith("[")) {
            JType componentType = parseFieldDescriptor(descr.substring(1),typeFactory);
            return new JArray(componentType);
        }

        return illegalStateDetected(descr);

    }

    private static JType parseType(String descr,Iterator<Character> iter, Function<String, JType> classFactory) {
        char c = iter.next();
        switch (c) {
            case 'I': return JPrimitiveType.INT;
            case 'Z': return JPrimitiveType.BOOLEAN;
            case 'C': return JPrimitiveType.CHAR;
            case 'B': return JPrimitiveType.BYTE;
            case 'S': return JPrimitiveType.SHORT;
            case 'F': return JPrimitiveType.FLOAT;
            case 'J': return JPrimitiveType.LONG;
            case 'D': return JPrimitiveType.DOUBLE;
            case '[': return parseArrayType(descr,iter, classFactory);
            case 'L': return parseRefType(descr,iter, classFactory);
            case ')': return null;
            case 'V': return JPrimitiveType.VOID;
        }

        return illegalStateDetected(descr);
    }

    private static JType parseRefType(String descr,Iterator<Character> iter, Function<String, JType> classFactory) {
        StringBuffer b = new StringBuffer();
        while (iter.hasNext()) {
            char c = iter.next();
            if (c==';') {
                // done
                String name = b.toString();
                return classFactory.apply(name);
            }
            if (c=='/') {
                c = '.';
            }
            b.append(c);
        }

        return illegalStateDetected(descr);
    }

    private static JType parseArrayType(String descr,Iterator<Character> iter, Function<String, JType> classFactory) {
        assert iter.hasNext();
        JType componentType = parseType(descr,iter,classFactory);
        return new JArray(componentType);
    }

    private static JType illegalStateDetected (String descriptor) {
        Preconditions.checkState(false,"Illegal state detected parsing descriptor " + descriptor);
        return null;
    }

}
