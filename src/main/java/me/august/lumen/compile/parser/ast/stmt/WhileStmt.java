package me.august.lumen.compile.parser.ast.stmt;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.codegen.Branch;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.Conditional;
import me.august.lumen.compile.codegen.MethodCodeGen;
import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.parser.ast.expr.Expression;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class WhileStmt implements CodeBlock, VisitorConsumer {

    private Expression condition;
    private Body body;

    public WhileStmt(Expression condition, Body body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void accept(ASTVisitor astVisitor) {
        condition.accept(astVisitor);
        body.accept(astVisitor);
    }

    private Branch toBranch() {
        // if the condition is already a Conditional, we have
        // to modify the condition and if-body code generation
        // slightly to fit the structure of a while-loop.
        if (condition instanceof Conditional) {
            Conditional cond = (Conditional) condition;
            Branch branch = cond.branch(body, null);

            Label reset = new Label();

            // hijack the condition to add "reset" label
            MethodCodeGen prevCond = branch.getCond();
            branch.setCond((m, c) -> {
                m.visitLabel(reset);
                prevCond.generate(m, c);
            });

            // hijack the body to include a GOTO
            MethodCodeGen prevIfBranch = branch.getIfBranch();
            branch.setIfBranch((m, c) -> {
                prevIfBranch.generate(m, c);
                m.visitJumpInsn(Opcodes.GOTO, reset);
            });

            return branch;

        // if the condition is not a Conditional, but still a boolean,
        // compare the condition to 0 (false), and jump to the top
        // (at the end of the body).
        } else if (condition.expressionType().getSort() == Type.BOOLEAN) {
            Label label = new Label();
            Label reset = new Label();
            MethodCodeGen cond = (m, c) -> {
                m.visitLabel(reset);
                condition.generate(m, c);
                m.visitJumpInsn(Opcodes.IFEQ, label);
            };
            MethodCodeGen branchBody = (m, c) -> {
                body.generate(m, c);
                m.visitJumpInsn(Opcodes.GOTO, reset);

            };
            return new Branch(cond, label, branchBody, null);
        } else {
            throw new IllegalArgumentException("Incompatible types.");
        }
    }


    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        toBranch().generate(visitor, context);
    }

    @Override
    public String toString() {
        return "WhileStatement{" +
            "condition=" + condition +
            ", body=" + body +
            '}';
    }
}
