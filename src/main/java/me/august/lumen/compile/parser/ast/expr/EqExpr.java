package me.august.lumen.compile.parser.ast.expr;

public class EqExpr extends BinaryExpression {

    public enum Op {
        EQ, NE
    }

    private Op op;

    public EqExpr(Expression left, Expression right, Op op) {
        super(left, right);
        this.op = op;
    }
}
