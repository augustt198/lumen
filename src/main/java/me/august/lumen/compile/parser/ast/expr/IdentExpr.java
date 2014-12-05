package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.analyze.var.Variable;

public class IdentExpr extends TerminalExpression {

    private String identifier;

    private Variable ref;

    public IdentExpr(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Variable getRef() {
        return ref;
    }

    public void setRef(Variable ref) {
        this.ref = ref;
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

    @Override
    public String toString() {
        return "IdentExpr{" +
            "identifier='" + identifier + '\'' +
            '}';
    }
}
