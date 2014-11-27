package me.august.lumen.compile.parser.ast.code;

import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.parser.ast.expr.Expression;

public class VarDeclaration implements CodeBlock {

    private String name;
    private String type;

    private Expression defaultValue;

    public VarDeclaration(String name, String type) {
        this(name, type, null);
    }

    public VarDeclaration(String name, String type, Expression defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Expression getDefaultValue() {
        return defaultValue;
    }


}
