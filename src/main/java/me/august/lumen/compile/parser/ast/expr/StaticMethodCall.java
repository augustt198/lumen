package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.parser.ast.Typed;

import java.util.List;

public class StaticMethodCall extends Typed implements Expression {

    private String className;
    private String methodName;

    private List<Expression> parameters;

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

    public List<Expression> getParameters() {
        return parameters;
    }
}
