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

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumExpr numExpr = (NumExpr) o;

        if (Double.compare(numExpr.value, value) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(value);
        return (int) (temp ^ (temp >>> 32));
    }
}
