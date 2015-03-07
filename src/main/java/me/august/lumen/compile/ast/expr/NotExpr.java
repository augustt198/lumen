package me.august.lumen.compile.ast.expr;

import me.august.lumen.compile.codegen.Branch;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.Conditional;
import me.august.lumen.compile.codegen.MethodCodeGen;
import me.august.lumen.compile.parser.ast.expr.UnaryExpression;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class NotExpr extends UnaryExpression implements Conditional {

    public NotExpr(Expression operand) {
        super(operand);
    }

    @Override
    public Branch branch(MethodCodeGen ifBranch, MethodCodeGen elseBranch) {
        Label label = new Label();
        MethodCodeGen cond = (m, c) -> {
            operand.generate(m, c);
            m.visitJumpInsn(Opcodes.IFNE, label);
        };
        return new Branch(cond, label, ifBranch, elseBranch);
    }

    @Override
    public Type expressionType() {
        return Type.BOOLEAN_TYPE;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        branch(Conditional.PUSH_TRUE, Conditional.PUSH_FALSE).generate(visitor, context);
    }

}
