package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.scanner.Op;

public class RelExpr extends BinaryExpression {

    public RelExpr(Expression left, Expression right, Op op) {
        super(left, right, op);
    }

}
