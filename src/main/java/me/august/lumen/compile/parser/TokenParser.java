package me.august.lumen.compile.parser;

import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.Type;

public interface TokenParser {

    default Expression parseExpression() {
        return parseExpression(0);
    }

    Expression parseExpression(int precedence);

    Token consume();
    Token peek();

    boolean accept(Type type);
    boolean expect(Type type);

    default UnresolvedType nextUnresolvedType() {
        String identifier = consume().expectType(Type.IDENTIFIER).getContent();
        int dimensions    = 0;

        while (accept(Type.L_BRACKET)) {
            consume().expectType(Type.R_BRACKET);
            dimensions++;
        }

        return new UnresolvedType(identifier, dimensions);
    }

    default String expectIdentifier() {
        Token token = consume();
        if (token.getType() != Type.IDENTIFIER) {
            throw new RuntimeException("Expected identifier");
        }

        return token.getContent();
    }

}
