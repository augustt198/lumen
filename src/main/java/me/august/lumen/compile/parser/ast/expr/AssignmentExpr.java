package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.scanner.Op;

public class AssignmentExpr extends BinaryExpression {

    public AssignmentExpr(IdentExpr right, Expression left) {
        super(right, left, Op.ASSIGN);
    }

}
