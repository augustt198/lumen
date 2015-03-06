package me.august.lumen.compile.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.codegen.Branch;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.Conditional;
import me.august.lumen.compile.codegen.MethodCodeGen;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class EqExpr extends BinaryExpression implements Conditional {

    public enum Op {
        EQ, NE
    }

    private Op op;

    public EqExpr(Expression left, Expression right, Op op) {
        super(left, right);
        this.op = op;
    }

    @Override
    public Branch branch(MethodCodeGen ifBranch, MethodCodeGen elseBranch) {
        Label label = new Label();
        MethodCodeGen cond = null;

        Type lType = left.expressionType();
        Type rType = right.expressionType();

        // if the left side is null and the right side is an
        // object, use IFNONNULL or IFNULL with the right side
        // value on the operand stack
        if (left instanceof NullExpr && BytecodeUtil.isPrimitive(rType)) {
            int opcode = op == Op.EQ ? Opcodes.IFNONNULL : Opcodes.IFNULL;
            cond = (m, c) -> {
                right.generate(m, c);
                m.visitJumpInsn(opcode, label);
            };

        // if the right side is null and the left side is an
        // object, use IFNONNULL or IFNULL with the left side
        // value on the operand stack
        } else if (right instanceof NullExpr && BytecodeUtil.isObject(lType)) {
            int opcode = op == Op.EQ ? Opcodes.IFNONNULL : Opcodes.IFNULL;
            cond = (m, c) -> {
                left.generate(m, c);
                m.visitJumpInsn(opcode, label);
            };

        // if the left and right side are both objects, use
        // IF_ACMPEQ or IF_ACMPNE with both values on the
        // operand stack
        } else if (BytecodeUtil.isObject(lType) && BytecodeUtil.isObject(rType)) {
            int opcode = op == Op.EQ ? Opcodes.IF_ACMPNE : Opcodes.IF_ACMPEQ;
            cond = (m, c) -> {
                left.generate(m, c);
                right.generate(m, c);
                m.visitJumpInsn(opcode, label);
            };

        // if the left and right side are both numeric primitives:
        // 1) get the widest type of the two
        // 2) promote all types narrower than int to int
        // 3) cast left and right to widest type if necessary
        // 4) if the widest type is a double, float, or long get
        //    the comparison opcode needed
        // 5) jump using IFNE/IFEQ or IF_ICMPNE/IF_ICMPEQ
        } else if (BytecodeUtil.isNumeric(lType) && BytecodeUtil.isNumeric(rType)) {
            Type widest = BytecodeUtil.widest(lType, rType);

            if (widest.getSort() < Type.INT) widest = Type.INT_TYPE;
            if (lType.getSort() < Type.INT) lType = Type.INT_TYPE;
            if (rType.getSort() < Type.INT) rType = Type.INT_TYPE;

            int lcast = BytecodeUtil.castNumberOpcode(lType, widest);
            int rcast = BytecodeUtil.castNumberOpcode(rType, widest);

            int cmp = BytecodeUtil.compareOpcode(widest);
            final int opcode;
            if (cmp >= 0) {
                opcode = op == Op.EQ ? Opcodes.IFNE : Opcodes.IFEQ;
            } else {
                opcode = op == Op.EQ ? Opcodes.IF_ICMPNE : Opcodes.IF_ICMPEQ;
            }

            cond = (m, c) -> {
                left.generate(m, c);
                if (lcast >= 0) m.visitInsn(lcast);

                right.generate(m, c);
                if (rcast >= 0) m.visitInsn(rcast);

                if (cmp >= 0) m.visitInsn(cmp);

                m.visitJumpInsn(opcode, label);
            };
        }

        return new Branch(cond, label, ifBranch, elseBranch);
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        branch(Conditional.PUSH_TRUE, Conditional.PUSH_FALSE).generate(visitor, context);
    }

}
