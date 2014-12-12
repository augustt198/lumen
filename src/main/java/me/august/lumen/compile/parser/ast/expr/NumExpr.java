package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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

    // TODO make it work for other numeric types
    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        BytecodeUtil.pushInt(visitor, (int) value);
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
