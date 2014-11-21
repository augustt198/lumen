package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.scanner.Op;
import org.objectweb.asm.MethodVisitor;

public abstract class BinaryExpression implements Expression {

    protected Expression left;
    protected Expression right;

    protected Op op;

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

    @Override
    public boolean isConstant() {
        return left.isConstant() && right.isConstant();
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        throw new UnsupportedOperationException();
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
