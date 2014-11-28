package me.august.lumen.compile.parser.ast.expr;

public class FalseExpr extends TerminalExpression {

    @Override
    public boolean isConstant() {
        return true;
    }
}
