package me.august.lumen.compile.parser.ast.expr;

import org.objectweb.asm.Type;

public class FalseExpr extends TerminalExpression {

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public Type expressionType() {
        return Type.BOOLEAN_TYPE;
    }
}
