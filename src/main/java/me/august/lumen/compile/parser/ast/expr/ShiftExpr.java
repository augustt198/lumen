package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.scanner.Op;

public class ShiftExpr extends BinaryExpression {

    public ShiftExpr(Expression left, Expression right, Op op) {
        super(left, right, op);
    }
}
