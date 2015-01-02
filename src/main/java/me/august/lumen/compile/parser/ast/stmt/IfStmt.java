package me.august.lumen.compile.parser.ast.stmt;

import jdk.internal.org.objectweb.asm.Opcodes;
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
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;

public class IfStmt implements CodeBlock, VisitorConsumer {

    private static Branch toBranch(Expression expr, MethodCodeGen ifBranch, MethodCodeGen elseBranch) {
        if (expr instanceof Conditional) {
            return ((Conditional) expr).branch(ifBranch, elseBranch);
        } else if (expr.expressionType().getSort() == Type.BOOLEAN) {
            Label label = new Label();
            MethodCodeGen cond = (m, c) -> {
                expr.generate(m, c);
                m.visitJumpInsn(Opcodes.IFEQ, label);
            };
            return new Branch(cond, label, ifBranch, elseBranch);
        } else {
            throw new IllegalArgumentException("Incompatible types.");
        }
    }

    private Expression condition;
    private Body trueBody;
    private List<ElseIf> elseIfs;
    private Body elseBody;

    public IfStmt(Expression condition, Body trueBody, List<ElseIf> elseIfs, Body elseBody) {
        this.condition = condition;
        this.trueBody = trueBody;
        this.elseIfs = elseIfs;
        this.elseBody = elseBody;
    }

    public IfStmt(Expression condition, Body trueBody) {
        this(condition, trueBody, new ArrayList<>(), null);
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        Branch branch = toBranch(condition, trueBody, elseBody);

        if (elseIfs != null) {
            for (ElseIf elseIf : elseIfs) {
                branch.setElseBranch(
                    toBranch(elseIf.condition, elseIf.body, branch.getElseBranch())
                );
            }
        }

        branch.generate(visitor, context);
    }

    public Expression getCondition() {
        return condition;
    }

    public Body getTrueBody() {
        return trueBody;
    }

    public List<ElseIf> getElseIfs() {
        return elseIfs;
    }

    public Body getElseBody() {
        return elseBody;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        condition.accept(visitor);
        trueBody.accept(visitor);

        for (ElseIf eif : elseIfs) {
            eif.condition.accept(visitor);
            eif.body.accept(visitor);
        }

        if (elseBody != null) elseBody.accept(visitor);
    }

    public static class ElseIf {
        private Expression condition;
        private Body body;

        public ElseIf(Expression condition, Body body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public String toString() {
            return "ElseIf{" +
                "condition=" + condition +
                ", body=" + body +
                '}';
        }
    }

    @Override
    public String toString() {
        return "IfStatement{" +
            "condition=" + condition +
            ", trueBody=" + trueBody +
            ", elseIfs=" + elseIfs +
            ", elseBody=" + elseBody +
            '}';
    }
}
