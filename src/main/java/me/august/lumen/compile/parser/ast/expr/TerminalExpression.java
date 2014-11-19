package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.scanner.Op;

public abstract class TerminalExpression implements Expression {

    @Override
    public Expression[] getChildren() {
        return new Expression[0];
    }

    @Override
    public Op getOp() {
        return null;
    }

}
