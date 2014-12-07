package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.common.Modifier;
import me.august.lumen.compile.parser.ast.Typed;
import me.august.lumen.compile.parser.ast.code.Body;

import java.util.*;

public class MethodNode extends Typed implements VisitorConsumer {

    private String name;
    private Modifier[] modifiers;

    private Map<String, String> parameters = new HashMap<>();

    private Body body;

    public MethodNode(String name, String returnType, Modifier... modifiers) {
        super(returnType);
        this.name = name;
        this.modifiers = modifiers;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitMethod(this);
        body.accept(visitor);
        visitor.visitMethodEnd(this);
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
            ", type='" + type + '\'' +
            ", body=" + body +
            '}';
    }
}
