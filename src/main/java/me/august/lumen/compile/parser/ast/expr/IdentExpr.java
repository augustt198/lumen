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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentExpr identExpr = (IdentExpr) o;

        if (identifier != null ? !identifier.equals(identExpr.identifier) : identExpr.identifier != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }
}
