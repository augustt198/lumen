package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.scanner.Op;

public class BitAndExpr extends BinaryExpression {

    public BitAndExpr(Expression left, Expression right) {
        super(left, right, Op.BIT_AND);
    }
}
