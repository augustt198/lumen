package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.parser.ast.Typed;

public class CastExpr extends Typed implements Expression {

    private Expression value;

    public CastExpr(Expression value, String type) {
        super(type);
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{value};
    }
}
