package me.august.lumen.compile.parser.ast.expr.eval;

import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.scanner.Op;

public class TernaryExpr implements Expression {

    private Expression condition;

    private Expression trueExpr;
    private Expression falseExpr;

    public TernaryExpr(Expression condition, Expression trueExpr, Expression falseExpr) {
        this.condition = condition;
        this.trueExpr = trueExpr;
        this.falseExpr = falseExpr;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getTrueExpr() {
        return trueExpr;
    }

    public Expression getFalseExpr() {
        return falseExpr;
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{condition, trueExpr, falseExpr};
    }

    @Override
    public Op getOp() {
        return null;
    }

    @Override
    public boolean isConstant() {
        return condition.isConstant() && trueExpr.isConstant() && falseExpr.isConstant();
    }

    @Override
    public String toString() {
        return "TernaryExpr{" +
            "condition=" + condition +
            ", trueExpr=" + trueExpr +
            ", falseExpr=" + falseExpr +
            '}';
    }
}
