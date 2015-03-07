package me.august.lumen.compile.ast.stmt;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.ast.CodeBlock;
import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.codegen.Branch;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.Conditional;
import me.august.lumen.compile.codegen.MethodCodeGen;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

public class IfStmt implements CodeBlock, VisitorConsumer {

    private static Branch toBranch(Expression expr, MethodCodeGen ifBranch, MethodCodeGen elseBranch) {
        if (expr instanceof Conditional) {
            return ((Conditional) expr).branch(ifBranch, elseBranch);
        } else if (expr.expressionType().getSort() == Type.BOOLEAN) {
            return Branch.branchBoolean(expr, ifBranch, elseBranch);
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
    public void acceptTopDown(ASTVisitor visitor) {
        condition.acceptTopDown(visitor);
        trueBody.acceptTopDown(visitor);

        for (ElseIf eif : elseIfs) {
            eif.condition.acceptTopDown(visitor);
            eif.body.acceptTopDown(visitor);
        }

        if (elseBody != null) elseBody.acceptTopDown(visitor);
    }

    @Override
    public void acceptBottomUp(ASTVisitor visitor) {
        condition.acceptBottomUp(visitor);
        trueBody.acceptBottomUp(visitor);

        for (ElseIf eif : elseIfs) {
            eif.condition.acceptBottomUp(visitor);
            eif.body.acceptBottomUp(visitor);
        }

        if (elseBody != null) elseBody.acceptBottomUp(visitor);
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
