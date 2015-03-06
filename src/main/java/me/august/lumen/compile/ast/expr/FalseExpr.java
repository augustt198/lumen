package me.august.lumen.compile.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class FalseExpr extends TerminalExpression {

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        visitor.visitInsn(Opcodes.ICONST_0); // load 0 (false)
    }

    @Override
    public Type expressionType() {
        return Type.BOOLEAN_TYPE;
    }
}
