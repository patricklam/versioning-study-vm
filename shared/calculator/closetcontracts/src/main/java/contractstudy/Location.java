package contractstudy;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;

import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Representation of a location within a compilation unit.
 * For methods parameters and return type, type parameters are removed. The location consists of the compilation unit, the name of the class, the
 * package name, nested inner classes if there are any, and the name and parameter type of methods, and a line  number if there is any.
 * This information is sufficient to cross-reference this with (information extracted from) bytecode.
 * @author jens dietrich
 */
public class Location {
    private String cuName = null;
    private String packageName = null;
    private String className = null;
    private Stack<String> innerClassNames = null;
    private String methodName = null;
    private List<String> methodParameterNames = null;
    private int lineNo = -1;


    public Location(String cuName, String packageName, String className, Stack<String> innerClassNames,MethodDeclaration methodDeclr) {
        this.cuName = cuName;
        this.packageName = packageName;
        this.className = className;
        this.innerClassNames = innerClassNames;

        this.lineNo = methodDeclr.getBeginLine();
        this.methodName = methodDeclr.getName();
        this.methodParameterNames = methodDeclr.getParameters()
            .stream()
            .map(param -> removeTypeParams(param))
            .collect(Collectors.toList());
    }

    public Location(String cuName, String packageName, String className, Stack<String> innerClassNames, ConstructorDeclaration constructorDeclr) {
        this.cuName = cuName;
        this.packageName = packageName;
        this.className = className;
        this.innerClassNames = innerClassNames;

        this.lineNo = constructorDeclr.getBeginLine();
        this.methodName = "<init>";
        this.methodParameterNames = constructorDeclr.getParameters()
            .stream()
            .map(param -> removeTypeParams(param))
            .collect(Collectors.toList());
    }

    private String removeTypeParams(Parameter param) {
        Type type = param.getType();


        return type.toStringWithoutComments();
    }

    // This is a common string representation.
    public String getMethodDeclaration() {
        // return packageName + '.' + className + "::" + methodName + '(' + this.methodParameterNames.stream().collect(Collectors.joining(",")) + ')';
        return methodName + '(' + this.methodParameterNames.stream().collect(Collectors.joining(",")) + ')';
    }

    public String getCuName() {
        return cuName;
    }

    public void setCuName(String cuName) {
        this.cuName = cuName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Stack<String> getInnerClassNames() {
        return innerClassNames;
    }

    public void setInnerClassNames(Stack<String> innerClassNames) {
        this.innerClassNames = innerClassNames;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getMethodParameterNames() {
        return methodParameterNames;
    }

    public void setMethodParameterNames(List<String> methodParameterNames) {
        this.methodParameterNames = methodParameterNames;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(cuName, location.cuName) &&
                Objects.equals(packageName, location.packageName) &&
                Objects.equals(className, location.className) &&
                Objects.equals(innerClassNames, location.innerClassNames) &&
                Objects.equals(methodName, location.methodName) &&
                Objects.equals(methodParameterNames, location.methodParameterNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cuName, packageName, className, innerClassNames, methodName, methodParameterNames);
    }
}
