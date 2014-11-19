package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.scanner.Op;

public class AssignmentExpr implements Expression {

    private Expression right;
    private Expression left;

    public AssignmentExpr(Expression right, Expression left) {
        this.right = right;
        this.left = left;
    }


    @Override
    public Expression[] getChildren() {
        return new Expression[]{right, left};
    }

    @Override
    public Op getOp() {
        return Op.ASSIGN;
    }
}
