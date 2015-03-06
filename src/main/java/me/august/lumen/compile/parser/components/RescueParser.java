package me.august.lumen.compile.parser.components;

import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.ast.expr.IdentExpr;
import me.august.lumen.compile.parser.ast.expr.RescueExpr;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.TokenType;

public class RescueParser implements InfixParser {

    @Override
    public Expression parse(TokenParser parser, Expression left, Token token) {
        Expression catchExpr;
        UnresolvedType type = null;

        if (parser.peek().getType() == TokenType.IDENTIFIER) {
            String identifier = parser.consume().getContent();
            if (parser.accept(TokenType.R_ARROW)) {
                type = new UnresolvedType(identifier);
                catchExpr = parser.parseExpression(getPrecedence());
            } else {
                catchExpr = new IdentExpr(identifier);
            }
        } else {
            catchExpr = parser.parseExpression(getPrecedence());
        }

        RescueExpr expr = new RescueExpr(type, left, catchExpr);
        if (type == null) {
            expr.setResolvedType(org.objectweb.asm.Type.getType(Exception.class));
        }

        return expr;
    }

    @Override
    public int getPrecedence() {
        return Precedence.RESCUE.getLevel();
    }
}
