package me.august.lumen.compile.parser.parsers;

import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.ast.expr.IdentExpr;
import me.august.lumen.compile.ast.expr.RescueExpr;
import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.resolve.type.BasicType;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.TokenType;

public class RescueParser implements InfixParser {

    @Override
    public Expression parse(TokenParser parser, Expression left, Token token) {
        Expression catchExpr;
        BasicType type = null;

        if (parser.peek().getType() == TokenType.IDENTIFIER) {
            String identifier = parser.consume().getContent();
            if (parser.accept(TokenType.R_ARROW)) {
                type = new BasicType(identifier);
                catchExpr = parser.parseExpression(getPrecedence());
            } else {
                catchExpr = new IdentExpr(identifier);
            }
        } else {
            catchExpr = parser.parseExpression(getPrecedence());
        }

        if (type == null) {
            type = new BasicType("Exception");
        }
        return new RescueExpr(type, left, catchExpr);
    }

    @Override
    public int getPrecedence() {
        return Precedence.RESCUE.getLevel();
    }
}
