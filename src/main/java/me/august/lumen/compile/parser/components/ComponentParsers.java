package me.august.lumen.compile.parser.components;

import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.parser.ast.expr.*;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.Type;
import me.august.lumen.compile.scanner.tokens.NumberToken;
import me.august.lumen.compile.scanner.tokens.StringToken;

public final class ComponentParsers {

    public static final NumberParser NUMBER_PARSER = new NumberParser();

    public static final GroupingParser GROUPING_PARSER = new GroupingParser();

    public static final StringParser STRING_PARSER = new StringParser();

    public static final BooleanParser BOOLEAN_PARSER = new BooleanParser();

    public static final NullParser NULL_PARSER = new NullParser();

    private ComponentParsers() {}

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

    private static class StringParser implements PrefixParser {

        @Override
        public Expression parse(TokenParser parser, Token token) {
            StringToken stringTok = (StringToken) token;
            return new StringExpr(token.getContent(), stringTok.getQuoteType());
        }

    }

    private static class BooleanParser implements PrefixParser {
        @Override
        public Expression parse(TokenParser parser, Token token) {
            if (token.getType() == Type.TRUE) {
                return new TrueExpr();
            } else {
                return new FalseExpr();
            }
        }
    }

    private static class NullParser implements PrefixParser {
        @Override
        public Expression parse(TokenParser parser, Token token) {
            return new NullExpr();
        }
    }

}