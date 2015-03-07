package me.august.lumen.compile.parser.parsers;

import me.august.lumen.compile.ast.expr.BitAndExpr;
import me.august.lumen.compile.ast.expr.BitOrExpr;
import me.august.lumen.compile.ast.expr.BitXorExpr;
import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.scanner.Token;

public final class BitwiseParsers {

    private BitwiseParsers() {}

    public static class BitwiseOrParser implements InfixParser {
        @Override
        public Expression parse(TokenParser parser, Expression left, Token token) {
            Expression right = parser.parseExpression(getPrecedence());
            return new BitOrExpr(left, right);
        }

        @Override
        public int getPrecedence() {
            return Precedence.BIT_OR.getLevel();
        }
    }

    public static class BitwiseXorParser implements InfixParser {
        @Override
        public Expression parse(TokenParser parser, Expression left, Token token) {
            Expression right = parser.parseExpression(getPrecedence());
            return new BitXorExpr(left, right);
        }

        @Override
        public int getPrecedence() {
            return Precedence.BIT_XOR.getLevel();
        }
    }

    public static class BitwiseAndParser implements InfixParser {
        @Override
        public Expression parse(TokenParser parser, Expression left, Token token) {
            Expression right = parser.parseExpression(getPrecedence());
            return new BitAndExpr(left, right);
        }

        @Override
        public int getPrecedence() {
            return Precedence.BIT_AND.getLevel();
        }
    }

}
