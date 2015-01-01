package me.august.lumen.compile.parser.ast.stmt;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.Conditional;
import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.parser.ast.expr.Expression;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;

public class IfStmt implements CodeBlock {

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
        ((Conditional) condition).branch(trueBody, elseBody)
            .generate(visitor, context);
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
