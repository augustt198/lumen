package me.august.lumen.compile.ast.expr;

public abstract class TerminalExpression implements Expression {

    @Override
    public Expression[] getChildren() {
        return new Expression[0];
    }

}
