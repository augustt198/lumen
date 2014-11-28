package me.august.lumen.compile.parser.ast.code;

import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.parser.ast.expr.Expression;

public class WhileStatement implements CodeBlock {

    private Expression condition;
    private Body body;

    public WhileStatement(Expression condition, Body body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return "WhileStatement{" +
            "condition=" + condition +
            ", body=" + body +
            '}';
    }
}
