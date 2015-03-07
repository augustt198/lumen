package me.august.lumen.compile.parser.ast.expr;

public class UnaryExpression implements Expression {

    protected Expression operand;

    public UnaryExpression(Expression operand) {
        this.operand = operand;
    }

    public Expression getOperand() {
        return operand;
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{operand};
    }
}
