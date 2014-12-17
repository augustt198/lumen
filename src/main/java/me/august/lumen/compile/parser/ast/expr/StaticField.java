package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.parser.ast.Typed;

public class StaticField extends Typed implements Expression {

    private String className;
    private String fieldName;

    public StaticField(String className, String fieldName) {
        super(className);
        this.className = className;
        this.fieldName = fieldName;
    }

    public String getClassName() {
        return className;
    }

    public String getFieldName() {
        return fieldName;
    }
}
