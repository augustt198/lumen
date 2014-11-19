package me.august.lumen.compile.parser.ast;

import me.august.lumen.common.Modifier;
import me.august.lumen.compile.parser.ast.expr.Expression;

import java.util.Arrays;

public class FieldNode {

    private String name;
    private String type;
    private Modifier[] modifiers;
    private Expression defaultValue;

    public FieldNode(String name, String type, Modifier... modifiers) {
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
    }

    public Expression getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Expression defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Modifier[] getModifiers() {
        return modifiers;
    }

    @Override
    public String toString() {
        return "FieldNode{" +
            "name='" + name + '\'' +
            ", type='" + type + '\'' +
            ", modifiers=" + Arrays.toString(modifiers) +
            '}';
    }
}
