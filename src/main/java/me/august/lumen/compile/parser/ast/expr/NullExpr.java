package me.august.lumen.compile.parser.ast.expr;

public class NullExpr extends TerminalExpression {

    @Override
    public boolean isConstant() {
        return true;
    }
}
