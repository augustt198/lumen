package me.august.lumen.compile.parser.parsers;

import me.august.lumen.compile.ast.expr.EqExpr;
import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.scanner.Token;

public class EqualityParser implements InfixParser {

    @Override
    public Expression parse(TokenParser parser, Expression left, Token token) {
        Expression right = parser.parseExpression(getPrecedence());
        switch (token.getType()) {
            case EQ:
                return new EqExpr(left, right, EqExpr.Op.EQ);
            case NE:
                return new EqExpr(left, right, EqExpr.Op.NE);
            default:
                throw new RuntimeException("Unexpected token: " + token);
        }
    }

    @Override
    public int getPrecedence() {
        return Precedence.EQUALITY.getLevel();
    }
}
