package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.MethodCodeGen;
import me.august.lumen.compile.parser.ast.Typed;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.List;

public class StaticMethodCall extends Typed implements Expression {

    private String className;
    private String methodName;

    private List<Expression> parameters;

    private MethodCodeGen ref;
    private Type returnType;

    public StaticMethodCall(String className, String methodName, List<Expression> parameters) {
        super(className);
        this.className = className;
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public MethodCodeGen getRef() {
        return ref;
    }

    public void setRef(MethodCodeGen ref) {
        this.ref = ref;
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        for (Expression arg : parameters)
            arg.generate(visitor, context);

        ref.generate(visitor, context);
    }

    @Override
    public Expression[] getChildren() {
        return parameters.toArray(new Expression[parameters.size()]);
    }

    @Override
    public Type expressionType() {
        return returnType;
    }

    @Override
    public String toString() {
        return "StaticMethodCall{" +
            "className='" + className + '\'' +
            ", methodName='" + methodName + '\'' +
            ", parameters=" + parameters +
            ", ref=" + ref +
            '}';
    }
}
