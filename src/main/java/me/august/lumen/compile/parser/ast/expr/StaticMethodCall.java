package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.MethodCodeGen;
import me.august.lumen.compile.parser.ast.Typed;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class StaticMethodCall extends Typed implements Expression {

    private String className;
    private String methodName;

    private List<Expression> parameters;

    private MethodCodeGen ref;

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

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        for (Expression arg : parameters)
            arg.generate(visitor, context);

        ref.generate(visitor, context);
    }
}
