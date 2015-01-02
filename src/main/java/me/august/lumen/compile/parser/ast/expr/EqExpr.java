package me.august.lumen.compile.parser.ast.expr;

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
        }

        return new Branch(cond, label, ifBranch, elseBranch);
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        branch(Conditional.PUSH_TRUE, Conditional.PUSH_FALSE).generate(visitor, context);
    }

}
