package me.august.lumen.compile.parser.components;

import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.parser.ast.expr.AddExpr;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.scanner.Token;

public class AdditiveParser implements InfixParser {

    @Override
    public Expression parse(TokenParser parser, Expression left, Token token) {
        Expression right = parser.parseExpression(getPrecedence());

        switch (token.getType()) {
            case PLUS:
                return new AddExpr(left, right, AddExpr.Op.ADD);
            case MIN:
                return new AddExpr(left, right, AddExpr.Op.SUB);
            default:
                throw new RuntimeException("Unexpected token: " + token);
        }
    }

    @Override
    public int getPrecedence() {
        return Precedence.ADDITIVE.getLevel();
    }

}
