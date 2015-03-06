package me.august.lumen.compile.ast.expr;

public abstract class BinaryExpression implements Expression {

    protected Expression left;
    protected Expression right;

    public BinaryExpression(Expression left, Expression right) {
        this.left   = left;
        this.right  = right;
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{left, right};
    }

    @Override
    public boolean isConstant() {
        return left.isConstant() && right.isConstant();
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '{' +
            "left=" + left +
            ", right=" + right +
            '}';
    }
}
