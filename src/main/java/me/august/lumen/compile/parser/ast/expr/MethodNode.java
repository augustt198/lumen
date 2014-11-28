package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.common.Modifier;
import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.parser.ast.code.Body;

import java.util.*;

public class MethodNode {

    private String name;
    private Modifier[] modifiers;
    private String returnType;

    private Map<String, String> parameters = new HashMap<>();

    private Body body;

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

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public boolean hasBody() {
        return body != null;
    }

    @Override
    public String toString() {
        return "MethodNode{" +
            "parameters=" + parameters +
            ", name='" + name + '\'' +
            ", modifiers=" + Arrays.toString(modifiers) +
            ", returnType='" + returnType + '\'' +
            ", body=" + body +
            '}';
    }
}
