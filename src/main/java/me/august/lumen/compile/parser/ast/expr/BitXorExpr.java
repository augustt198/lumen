package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.scanner.Op;

public class BitXorExpr extends BinaryExpression {

    public BitXorExpr(Expression left, Expression right) {
        super(left, right, Op.BIT_XOR);
    }
}
