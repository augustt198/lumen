package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class UnaryMinusExpr extends UnaryExpression {

    public UnaryMinusExpr(Expression operand) {
        super(operand);
    }

    @Override
    public Type expressionType() {
        return operand.expressionType();
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        operand.generate(visitor, context);

        Type type = expressionType();

        if (BytecodeUtil.isNumeric(type)) {
            if (type.getSort() < Type.INT)
                type = Type.INT_TYPE;

            visitor.visitInsn(BytecodeUtil.negateInstruction(type));
        } else {
            context.error("Incompatible types", false, this);
        }
    }

}
