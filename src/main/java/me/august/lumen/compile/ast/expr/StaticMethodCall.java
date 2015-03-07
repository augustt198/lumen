package me.august.lumen.compile.ast.expr;

import me.august.lumen.compile.ast.Typed;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.MethodCodeGen;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;

public class StaticMethodCall extends Typed implements Expression {

    private String methodName;

    private List<Expression> parameters;

    private MethodCodeGen ref;
    private Type returnType;

    private boolean pop;

    public StaticMethodCall(UnresolvedType classType, String methodName, List<Expression> parameters) {
        super(classType);
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public String getClassName() {
        return unresolvedType.getBaseName();
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
        if (pop)
            visitor.visitInsn(Opcodes.POP);
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
    public void markAsTopLevelStatement(boolean flag) {
        this.pop = flag;
    }

    @Override
    public String toString() {
        return "StaticMethodCall{" +
            "methodName='" + methodName + '\'' +
            ", parameters=" + parameters +
            ", ref=" + ref +
            ", returnType=" + returnType +
            ", pop=" + pop +
            '}';
    }
}
