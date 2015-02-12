package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class UnaryMinusExpr implements Expression {

    private Expression value;

    public UnaryMinusExpr(Expression value) {
        this.value = value;
    }

    @Override
    public Type expressionType() {
        return value.expressionType();
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        value.generate(visitor, context);

        Type type = expressionType();

        if (BytecodeUtil.isNumeric(type)) {
            if (type.getSort() < Type.INT)
                type = Type.INT_TYPE;

            visitor.visitInsn(BytecodeUtil.negateInstruction(type));
        } else {
            throw new RuntimeException("Incompatible types!");
        }
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{value};
    }
}
