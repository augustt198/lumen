package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class BitwiseComplementExpr extends UnaryExpression {

    public BitwiseComplementExpr(Expression operand) {
        super(operand);
    }

    @Override
    public Type expressionType() {
        Type type = operand.expressionType();

        if (type.getSort() <= Type.INT)
            return Type.INT_TYPE;
        else if (type.getSort() == Type.LONG)
            return Type.LONG_TYPE;
        else
            return null;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        Type type = expressionType();

        operand.generate(visitor, context);

        int opcode;
        if (type.getSort() == Type.INT) {
            opcode = Opcodes.IXOR;
        } else {
            opcode = Opcodes.LXOR;
        }

        // load -1 (all bits set)
        visitor.visitInsn(Opcodes.ICONST_M1);

        // ixor or lxor, depending on expression type
        visitor.visitInsn(opcode);
    }

}
