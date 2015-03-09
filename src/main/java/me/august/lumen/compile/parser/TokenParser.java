package me.august.lumen.compile.parser;

import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.resolve.type.BasicType;
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

    default BasicType nextUnresolvedType() {
        String identifier = consume().expectType(TokenType.IDENTIFIER).getContent();
        int dimensions    = 0;

        while (accept(TokenType.L_BRACKET)) {
            consume().expectType(TokenType.R_BRACKET);
            dimensions++;
        }

        return new BasicType(identifier, dimensions);
    }

    default String expectIdentifier() {
        Token token = consume();
        if (token.getType() != TokenType.IDENTIFIER) {
            throw new RuntimeException("Expected identifier");
        }

        return token.getContent();
    }

}
