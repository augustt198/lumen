package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.Typed;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class CastExpr extends Typed implements Expression {

    private Expression value;

    public CastExpr(Expression value, String type) {
        super(type);
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public Type expressionType() {
        return getResolvedType();
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{value};
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        value.generate(visitor, context);

        Type orig   = value.expressionType();
        Type target = getResolvedType();


        // ensure orig and target are either both primitive, or both objects
        if (BytecodeUtil.isPrimitive(orig) != BytecodeUtil.isPrimitive(target)) {
            throw new RuntimeException("Incomptable types");
        }

        // if both are primitives
        if (BytecodeUtil.isPrimitive(orig)) {
            if (orig.getSort() < Type.INT) {
                visitor.visitInsn(BytecodeUtil.castNumberOpcode(Type.INT_TYPE, target));
            } else if (orig.getSort() > Type.INT && target.getSort() < Type.INT) {
                visitor.visitInsn(BytecodeUtil.castNumberOpcode(orig, Type.INT_TYPE));
                visitor.visitInsn(BytecodeUtil.castNumberOpcode(Type.INT_TYPE, target));
            } else if (orig.getSort() >= Type.INT && target.getSort() >= Type.INT) {
                visitor.visitInsn(BytecodeUtil.castNumberOpcode(orig, target));
            }
        } else { // if both are objects, just checkcast
            visitor.visitTypeInsn(Opcodes.CHECKCAST, target.getInternalName());
        }
    }
}
