package me.august.lumen.compile.parser.ast.expr;

public class StaticField implements Expression {

    private String className;
    private String fieldName;

    public StaticField(String className, String fieldName) {
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
