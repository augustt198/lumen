package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.scanner.Op;

public class BinaryExpression implements Expression {

    private Expression left;
    private Expression right;

    private Op op;

    public BinaryExpression(Expression left, Expression right, Op op) {
        this.left   = left;
        this.right  = right;
        this.op     = op;
    }

    @Override
    public Op getOp() {
        return op;
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{left, right};
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "left=" + left +
            ", right=" + right +
            ", op=" + op +
            '}';
    }
}
