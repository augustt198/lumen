package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

// NOTE: AddExpr does NOT necessarily indicate addition,
// it is *additive* (which includes subtraction)
public class AddExpr extends BinaryExpression {

    public enum Op {
        ADD, SUB
    }

    private Type type;
    private Op op;

    public AddExpr(Expression left, Expression right, Op op) {
        super(left, right);
        this.op = op;
    }

    @Override
    public Type expressionType() {
        if (type != null) return type; // return if already cached

        Type leftType  = left.expressionType();
        Type rightType = right.expressionType();

        // if either operands is a String type, entire expression is a String type
        if (BytecodeUtil.isString(leftType) || BytecodeUtil.isString(rightType)) {
            return (type = BytecodeUtil.STRING_TYPE);
        }

        if (BytecodeUtil.isPrimitive(leftType) && BytecodeUtil.isPrimitive(rightType)) {
            Type widest = BytecodeUtil.widest(leftType, rightType);
            // if the widest of the two is less than int,
            // promote to int
            if (widest.getSort() < Type.INT) {
                widest = Type.INT_TYPE;
            }
            return (type = widest);
        }

        throw new RuntimeException("Incompatible operands: " + leftType + ", " + rightType);
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        if (BytecodeUtil.isString(expressionType())) {
            BytecodeUtil.concatStringsBytecode(
                visitor, context, left, right
            );
            return;
        }

        int opcode;
        Type leftType  = left.expressionType();
        Type rightType = right.expressionType();

        left.generate(visitor, context);
        // the opcode we need to convert the left hand value
        // to the target value
        opcode = BytecodeUtil.castNumberOpcode(leftType, expressionType());
        // opcode will be -1 if casting is not possible or no
        // casting is possible
        if (opcode >= 0)
            visitor.visitInsn(opcode);

        right.generate(visitor, context);
        opcode = BytecodeUtil.castNumberOpcode(rightType, expressionType());
        if (opcode >= 0)
            visitor.visitInsn(opcode);

        // add
        if (op == Op.ADD) {
            opcode = BytecodeUtil.addInstruction(expressionType());
        } else { // subtract
            opcode = BytecodeUtil.subtractInstruction(expressionType());
        }

        visitor.visitInsn(opcode);
    }


}
