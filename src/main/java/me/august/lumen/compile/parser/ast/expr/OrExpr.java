package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.scanner.Op;

public class OrExpr extends BinaryExpression {

    public OrExpr(Expression left, Expression right) {
        super(left, right, Op.LOGIC_OR);
    }

}
