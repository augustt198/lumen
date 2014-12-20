package me.august.lumen.compile.parser.ast.expr;

import org.objectweb.asm.Type;

public class NullExpr extends TerminalExpression {

    private static final Type OBJECT_TYPE = Type.getType(Object.class);

    @Override
    public boolean isConstant() {
        return true;
    }

    // null can be any object
    @Override
    public Type expressionType() {
        return OBJECT_TYPE;
    }
}
