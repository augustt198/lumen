package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.common.Modifier;

import java.util.*;

public class MethodNode {

    private Map<String, String> parameters = new HashMap<>();
    private List<Expression> expressions = new ArrayList<>();

    private String name;
    private Modifier[] modifiers;
    private String returnType;

    private boolean hasBody;

    public MethodNode(String name, String returnType, Modifier... modifiers) {
        this.name = name;
        this.returnType = returnType;
        this.modifiers = modifiers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Modifier[] getModifiers() {
        return modifiers;
    }

    public void setModifiers(Modifier[] modifiers) {
        this.modifiers = modifiers;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public boolean isHasBody() {
        return hasBody;
    }

    public void setHasBody(boolean hasBody) {
        this.hasBody = hasBody;
    }

    @Override
    public String toString() {
        return "MethodNode{" +
            "parameters=" + parameters +
            ", expressions=" + expressions +
            ", name='" + name + '\'' +
            ", modifiers=" + Arrays.toString(modifiers) +
            ", returnType='" + returnType + '\'' +
            '}';
    }
}
