package me.august.lumen.compile.parser.ast.stmt;

import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.parser.ast.expr.Expression;

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
