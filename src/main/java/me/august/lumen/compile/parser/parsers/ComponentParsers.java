package me.august.lumen.compile.parser.parsers;

import me.august.lumen.compile.ast.expr.*;
import me.august.lumen.compile.scanner.TokenType;
import me.august.lumen.compile.scanner.tokens.NumberToken;
import me.august.lumen.compile.scanner.tokens.StringToken;

public final class ComponentParsers {

    public static final PrefixParser NUMBER_PARSER = (parser, token) -> {
        if (token.getType() == TokenType.NUMBER) {
            NumberToken numTok = (NumberToken) token;
            return new NumExpr(numTok.getValue());
        } else {
            throw new RuntimeException("Unexpected token: " + token);
        }
    };

    public static final PrefixParser GROUPING_PARSER = (parser, token) -> {
        Expression expression = parser.parseExpression();

        if (parser.consume().getType() != TokenType.R_PAREN) {
            throw new RuntimeException("Expected right parenthesis");
        }

        return expression;
    };

    public static final PrefixParser STRING_PARSER = (parser, token) -> {
        StringToken stringTok = (StringToken) token;
        return new StringExpr(token.getContent(), stringTok.getQuoteType());
    };

    public static final PrefixParser BOOLEAN_PARSER = (parser, token) -> {
        if (token.getType() == TokenType.TRUE) {
            return new TrueExpr();
        } else {
            return new FalseExpr();
        }
    };

    public static final PrefixParser NULL_PARSER =
            (parser, token) -> new NullExpr();

    private ComponentParsers() {}


}
