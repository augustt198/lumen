package me.august.lumen.compile.parser.ast.stmt;

import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.parser.ast.expr.Expression;

public class WhileStmt implements CodeBlock {

    private Expression condition;
    private Body body;

    public WhileStmt(Expression condition, Body body) {
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
