package me.august.lumen.compile.parser.ast.expr;

public class RelExpr extends BinaryExpression {

    public enum Op {
        LT, LTE, GT, GTE
    }

    private Op op;

    public RelExpr(Expression left, Expression right, Op op) {
        super(left, right);
        this.op = op;
    }

}
