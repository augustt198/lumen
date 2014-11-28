package me.august.lumen.compile.parser.ast.expr;

public class TrueExpr extends TerminalExpression {

    @Override
    public boolean isConstant() {
        return true;
    }
}
