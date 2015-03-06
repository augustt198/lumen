package me.august.lumen.compile.parser.parsers;

import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.ast.expr.AndExpr;
import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.ast.expr.OrExpr;
import me.august.lumen.compile.scanner.Token;

public final class LogicParsers {

    private LogicParsers() {}

    public static class LogicOrParser implements InfixParser {
        @Override
        public Expression parse(TokenParser parser, Expression left, Token token) {
            Expression right = parser.parseExpression(getPrecedence());
            return new OrExpr(left, right);
        }

        @Override
        public int getPrecedence() {
            return Precedence.LOGIC_OR.getLevel();
        }
    }

    public static class LogicAndParser implements InfixParser {
        @Override
        public Expression parse(TokenParser parser, Expression left, Token token) {
            Expression right = parser.parseExpression(getPrecedence());
            return new AndExpr(left, right);
        }

        @Override
        public int getPrecedence() {
            return Precedence.LOGIC_AND.getLevel();
        }
    }

}
