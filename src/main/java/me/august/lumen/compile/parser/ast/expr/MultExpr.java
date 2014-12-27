package me.august.lumen.compile.parser.ast.expr;

public class MultExpr extends BinaryExpression {

    public enum Op {
        MULT, DIV, REM
    }

    private Op op;

    public MultExpr(Expression left, Expression right, Op op) {
        super(left, right);
        this.op = op;
    }
}
