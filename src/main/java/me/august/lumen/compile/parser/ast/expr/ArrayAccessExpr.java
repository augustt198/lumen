package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class ArrayAccessExpr implements Expression {

    private Expression value;
    private Expression index;

    public ArrayAccessExpr(Expression value, Expression index) {
        this.value = value;
        this.index = index;
    }

    @Override
    public Type expressionType() {
        if (value == null || value.expressionType() == null)
            return null;

        return value.expressionType().getElementType();
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        value.generate(visitor, context);
        index.generate(visitor, context);

        int opcode = BytecodeUtil.arrayLoadOpcode(expressionType());
        visitor.visitInsn(opcode);
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{value, index};
    }

}
