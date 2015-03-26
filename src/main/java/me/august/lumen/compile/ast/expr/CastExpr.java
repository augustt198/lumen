package me.august.lumen.compile.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.ast.SingleTypedNode;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.resolve.type.BasicType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class CastExpr extends SingleTypedNode implements Expression {

    private Expression value;

    public CastExpr(Expression value, BasicType type) {
        super(type);
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public Type expressionType() {
        //return getResolvedType();
        return null;
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{value};
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        value.generate(visitor, context);

        Type orig   = value.expressionType();
        Type target = getTypeInfo().getResolvedType();


        // ensure orig and target are either both primitive, or both objects
        if (BytecodeUtil.isPrimitive(orig) != BytecodeUtil.isPrimitive(target)) {
            context.error("Incompatible types", false, this);
        }

        // if both are primitives
        if (BytecodeUtil.isPrimitive(orig)) {
            // if the original type is shorter than an int, e.g.
            // byte -> double, we can just interpret it as
            // int -> double
            if (orig.getSort() < Type.INT) {
                visitor.visitInsn(BytecodeUtil.castNumberOpcode(Type.INT_TYPE, target));

            // if the original type is wider than an int, and the target type
            // is shorter than an int, we first convert the original type to
            // an int and then convert that int result to the target type.
            // e.g. double -> byte is interpreted as:
            // double -> int, then int -> byte
            } else if (orig.getSort() > Type.INT && target.getSort() < Type.INT) {
                visitor.visitInsn(BytecodeUtil.castNumberOpcode(orig, Type.INT_TYPE));
                visitor.visitInsn(BytecodeUtil.castNumberOpcode(Type.INT_TYPE, target));

            // if both types are wider (or equal) to an int, we can convert
            // them directly.
            // e.g. double -> int produces the D2I opcode
            } else if (orig.getSort() >= Type.INT && target.getSort() >= Type.INT) {
                visitor.visitInsn(BytecodeUtil.castNumberOpcode(orig, target));
            }
        } else { // if both are objects, just checkcast
            visitor.visitTypeInsn(Opcodes.CHECKCAST, target.getInternalName());
        }
    }
}
