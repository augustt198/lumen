package me.august.lumen.compile.parser.ast.expr;


import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class MultExpr extends BinaryExpression {

    public enum Op {
        MULT, DIV, REM
    }

    private Op op;
    private Type type;

    public MultExpr(Expression left, Expression right, Op op) {
        super(left, right);
        this.op = op;
    }

    @Override
    public Type expressionType() {
        if (type != null) return type;

        Type leftType  = left.expressionType();
        Type rightType = right.expressionType();

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
        int opcode;
        Type leftType  = left.expressionType();
        Type rightType = right.expressionType();

        left.generate(visitor, context);
        opcode = BytecodeUtil.castNumberOpcode(leftType, expressionType());
        if (opcode >= 0)
            visitor.visitInsn(opcode);

        right.generate(visitor, context);
        opcode = BytecodeUtil.castNumberOpcode(rightType, expressionType());
        if (opcode >= 0)
            visitor.visitInsn(opcode);

        if (op == Op.MULT) {
            opcode = BytecodeUtil.multiplyInstruction(expressionType());
        } else if (op == Op.DIV) {
            opcode = BytecodeUtil.divisionInstruction(expressionType());
        } else { // must be REM
            opcode = BytecodeUtil.remainderInstruction(expressionType());
        }

        visitor.visitInsn(opcode);
    }
}
