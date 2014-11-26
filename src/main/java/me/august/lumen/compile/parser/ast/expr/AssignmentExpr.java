package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.scanner.Op;

public class AssignmentExpr extends BinaryExpression {

    public AssignmentExpr(Expression right, Expression left) {
        super(right, left, Op.ASSIGN);
    }

}