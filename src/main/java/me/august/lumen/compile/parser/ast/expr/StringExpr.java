package me.august.lumen.compile.parser.ast.expr;

public class StringExpr extends TerminalExpression {

    private String string;

    public StringExpr(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return "StringExpr{" +
            "string='" + string + '\'' +
            '}';
    }

    @Override
    public boolean isConstant() {
        return true;
    }
}
