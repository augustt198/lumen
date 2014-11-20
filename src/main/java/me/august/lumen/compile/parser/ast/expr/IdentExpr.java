package me.august.lumen.compile.parser.ast.expr;

public class IdentExpr extends TerminalExpression {

    String identifier;

    public IdentExpr(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean isConstant() {
        return false;
    }
}
