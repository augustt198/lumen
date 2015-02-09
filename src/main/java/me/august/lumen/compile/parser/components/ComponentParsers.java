package me.august.lumen.compile.parser.components;

import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.ast.expr.IdentExpr;
import me.august.lumen.compile.parser.ast.expr.NumExpr;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.Type;
import me.august.lumen.compile.scanner.tokens.NumberToken;

public final class ComponentParsers {

    public static final IdentifierParser IDENTIFIER_PARSER = new IdentifierParser();

    public static final NumberParser NUMBER_PARSER = new NumberParser();

    public static final GroupingParser GROUPING_PARSER = new GroupingParser();

    private ComponentParsers() {}

    private static class IdentifierParser implements PrefixParser {

        @Override
        public Expression parse(TokenParser parser, Token token) {
            return new IdentExpr(token.getContent());
        }

    }

    private static class NumberParser implements PrefixParser {

        @Override
        public Expression parse(TokenParser parser, Token token) {
            if (token.getType() == Type.NUMBER) {
                NumberToken numTok = (NumberToken) token;
                return new NumExpr(numTok.getValue());
            } else {
                throw new RuntimeException("Unexpected token: " + token);
            }
        }

    }

    private static class GroupingParser implements PrefixParser {

        @Override
        public Expression parse(TokenParser parser, Token token) {
            Expression expression = parser.parseExpression();

            if (parser.consume().getType() != Type.R_PAREN) {
                throw new RuntimeException("Expected right parenthesis");
            }

            return expression;
        }

    }

}
