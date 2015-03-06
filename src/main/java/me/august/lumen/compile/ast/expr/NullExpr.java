package me.august.lumen.compile.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
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

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        visitor.visitInsn(Opcodes.ACONST_NULL);
    }
}
