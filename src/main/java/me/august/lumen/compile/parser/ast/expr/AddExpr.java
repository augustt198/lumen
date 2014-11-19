package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.scanner.Op;

public class AddExpr extends BinaryExpression {

    public AddExpr(Expression left, Expression right, Op op) {
        super(left, right, op);
    }
}
