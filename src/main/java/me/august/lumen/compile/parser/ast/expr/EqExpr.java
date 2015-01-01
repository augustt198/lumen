package me.august.lumen.compile.parser.ast.expr;

import com.sun.org.apache.bcel.internal.generic.IFNONNULL;
import jdk.internal.org.objectweb.asm.Type;
import me.august.lumen.compile.codegen.Branch;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.Conditional;
import me.august.lumen.compile.codegen.MethodCodeGen;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
        if (left instanceof NullExpr && right.expressionType().getSort() == Type.OBJECT) {
            int opcode = op == Op.EQ ? Opcodes.IFNONNULL : Opcodes.IFNULL;
            cond = (m, c) -> {
                right.generate(m, c);
                m.visitJumpInsn(opcode, label);
            };
        } else if (right instanceof NullExpr && left.expressionType().getSort() == Type.OBJECT) {
            int opcode = op == Op.EQ ? Opcodes.IFNONNULL : Opcodes.IFNULL;
            cond = (m, c) -> {
                left.generate(m, c);
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
