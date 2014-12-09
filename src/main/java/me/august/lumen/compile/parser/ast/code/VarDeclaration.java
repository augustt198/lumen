package me.august.lumen.compile.parser.ast.code;

import me.august.lumen.compile.analyze.var.LocalVariable;
import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.parser.ast.Typed;
import me.august.lumen.compile.parser.ast.expr.Expression;

public class VarDeclaration extends Typed implements CodeBlock {

    private String name;
    private Expression defaultValue;

    private LocalVariable ref;

    public VarDeclaration(String name, String type) {
        this(name, type, null);
    }

    public VarDeclaration(String name, String type, Expression defaultValue) {
        super(type);
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public Expression getDefaultValue() {
        return defaultValue;
    }

    public LocalVariable getRef() {
        return ref;
    }

    public void setRef(LocalVariable ref) {
        this.ref = ref;
    }

}
