package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.scanner.Op;

public class BitOrExpr extends BinaryExpression {

    public BitOrExpr(Expression left, Expression right) {
        super(left, right, Op.BIT_OR);
    }
}
