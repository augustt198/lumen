package me.august.lumen.compile.ast.stmt;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.codegen.Branch;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.Conditional;
import me.august.lumen.compile.codegen.MethodCodeGen;
import me.august.lumen.compile.ast.CodeBlock;
import me.august.lumen.compile.ast.expr.Expression;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class WhileStmt implements CodeBlock, VisitorConsumer, Loop {

    private Expression condition;
    private Body body;

    private Label repeat;
    private Label exit;

    public WhileStmt(Expression condition, Body body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Label getRepeatLabel() {
        return repeat;
    }

    @Override
    public Label getExitLabel() {
        return exit;
    }

    @Override
    public void acceptTopDown(ASTVisitor visitor) {
        visitor.visitWhileStmt(this);

        condition.acceptTopDown(visitor);
        body.acceptTopDown(visitor);
    }

    @Override
    public void acceptBottomUp(ASTVisitor visitor) {
        condition.acceptBottomUp(visitor);
        body.acceptBottomUp(visitor);

        visitor.visitWhileStmt(this);
    }

    private Branch toBranch() {
        // if the condition is already a Conditional, we have
        // to modify the condition and if-body code generation
        // slightly to fit the structure of a while-loop.
        if (condition instanceof Conditional) {
            Conditional cond = (Conditional) condition;
            Branch branch = cond.branch(body, null);

            repeat = new Label();
            exit   = branch.getElseJump();

            // hijack the condition to add "reset" label
            MethodCodeGen prevCond = branch.getCond();
            branch.setCond((m, c) -> {
                m.visitLabel(repeat);
                prevCond.generate(m, c);
            });

            // hijack the body to include a GOTO
            MethodCodeGen prevIfBranch = branch.getIfBranch();
            branch.setIfBranch((m, c) -> {
                prevIfBranch.generate(m, c);
                m.visitJumpInsn(Opcodes.GOTO, repeat);
            });

            return branch;

        // if the condition is not a Conditional, but still a boolean,
        // compare the condition to 0 (false), and jump to the top
        // (at the end of the body).
        } else if (condition.expressionType().getSort() == Type.BOOLEAN) {
            repeat = new Label();
            exit   = new Label();

            MethodCodeGen cond = (m, c) -> {
                m.visitLabel(repeat);
                condition.generate(m, c);
                m.visitJumpInsn(Opcodes.IFEQ, exit);
            };
            MethodCodeGen branchBody = (m, c) -> {
                body.generate(m, c);
                m.visitJumpInsn(Opcodes.GOTO, repeat);

            };
            return new Branch(cond, exit, branchBody, null);
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
