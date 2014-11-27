package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.common.Modifier;
import me.august.lumen.compile.parser.ast.CodeBlock;

import java.util.*;

public class MethodNode {

    private Map<String, String> parameters = new HashMap<>();
    private List<CodeBlock> code = new ArrayList<>();

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

    public List<CodeBlock> getCode() {
        return code;
    }

    public void setCode(List<CodeBlock> code) {
        this.code = code;
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

    public boolean hasBody() {
        return hasBody;
    }

    public void setHasBody(boolean hasBody) {
        this.hasBody = hasBody;
    }

    @Override
    public String toString() {
        return "MethodNode{" +
            "parameters=" + parameters +
            ", code=" + code +
            ", name='" + name + '\'' +
            ", modifiers=" + Arrays.toString(modifiers) +
            ", returnType='" + returnType + '\'' +
            '}';
    }
}
