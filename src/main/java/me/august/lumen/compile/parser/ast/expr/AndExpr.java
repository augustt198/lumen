package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.scanner.Op;

public class AndExpr extends BinaryExpression {

    public AndExpr(Expression left, Expression right) {
        super(left, right, Op.LOGIC_AND);
    }

}
