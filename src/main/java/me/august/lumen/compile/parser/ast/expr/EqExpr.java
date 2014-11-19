package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.scanner.Op;

public class EqExpr extends BinaryExpression {

    public EqExpr(Expression left, Expression right, Op op) {
        super(left, right, op);
        if (op != Op.EQ && op != Op.NE) throw new RuntimeException("Expected EQ or NE");
    }
}
