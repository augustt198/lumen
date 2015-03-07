package me.august.lumen.compile.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class NumExpr extends TerminalExpression {

    private Number value;
    private Type type;

    public NumExpr(Number value) {
        this.value = value;
        type = BytecodeUtil.numberType(value.getClass());
    }

    @Override
    public Type expressionType() {
        return type;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        // pushInt can handle byte, short, and int ranges
        if (value instanceof Integer || value instanceof Byte || value instanceof Short) {
            BytecodeUtil.pushInt(visitor, (int) value);
        } else if (value instanceof Long) {
            BytecodeUtil.pushLong(visitor, (long) value);
        } else if (value instanceof Float) {
            BytecodeUtil.pushFloat(visitor, (float) value);
        } else if (value instanceof Double) {
            BytecodeUtil.pushDouble(visitor, (double) value);
        }
    }

    public Number getValue() {
        return value;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public String toString() {
        return "NumExpr{" +
            "value=" + value +
            ", type=" + type +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumExpr numExpr = (NumExpr) o;

        if (type != null ? !type.equals(numExpr.type) : numExpr.type != null) return false;
        if (value != null ? !value.equals(numExpr.value) : numExpr.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
