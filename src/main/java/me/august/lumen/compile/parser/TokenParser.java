package me.august.lumen.compile.parser;

import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.TokenType;

public interface TokenParser {

    default Expression parseExpression() {
        return parseExpression(0);
    }

    Expression parseExpression(int precedence);

    Token consume();
    Token peek();

    boolean accept(TokenType type);
    boolean expect(TokenType type);

    default UnresolvedType nextUnresolvedType() {
        String identifier = consume().expectType(TokenType.IDENTIFIER).getContent();
        int dimensions    = 0;

        while (accept(TokenType.L_BRACKET)) {
            consume().expectType(TokenType.R_BRACKET);
            dimensions++;
        }

        return new UnresolvedType(identifier, dimensions);
    }

    default String expectIdentifier() {
        Token token = consume();
        if (token.getType() != TokenType.IDENTIFIER) {
            throw new RuntimeException("Expected identifier");
        }

        return token.getContent();
    }

}
