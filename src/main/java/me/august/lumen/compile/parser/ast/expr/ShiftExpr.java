package me.august.lumen.compile.parser.ast.expr;

public class ShiftExpr extends BinaryExpression {

    public enum Op {
        SH_L, SH_R, U_SHR_R
    }

    private Op op;

    public ShiftExpr(Expression left, Expression right, Op op) {
        super(left, right);
        this.op = op;
    }
}
