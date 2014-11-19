package me.august.lumen.compile.parser.ast.expr;

public class NumExpr extends TerminalExpression {

    double value;

    public NumExpr(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "NumExpr{" +
            "value=" + value +
            '}';
    }
}
