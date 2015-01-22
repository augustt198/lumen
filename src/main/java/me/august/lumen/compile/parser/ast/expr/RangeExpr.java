package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;

public class RangeExpr extends BinaryExpression {

    private boolean inclusive;

    public RangeExpr(Expression left, Expression right, boolean inclusive) {
        super(left, right);
        this.inclusive = inclusive;
    }

    public boolean isInclusive() {
        return inclusive;
    }

    public boolean isExclusive() {
        return !inclusive;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        throw new UnsupportedOperationException("Illegal use of range.");
    }

}
