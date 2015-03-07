package me.august.lumen.compile.parser.parsers;

import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.ast.expr.MultExpr;
import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.scanner.Token;

public class MultiplicativeParser implements InfixParser {

    @Override
    public Expression parse(TokenParser parser, Expression left, Token token) {
        MultExpr.Op op;
        switch (token.getType()) {
            case MULT:
                op = MultExpr.Op.MULT;
                break;
            case DIV:
                op = MultExpr.Op.DIV;
                break;
            case REM:
                op = MultExpr.Op.REM;
                break;
            default:
                throw new RuntimeException("Unexpected token: " + token);
        }

        Expression right = parser.parseExpression(getPrecedence());
        return new MultExpr(left, right, op);
    }

    @Override
    public int getPrecedence() {
        return Precedence.MULTIPLICATIVE.getLevel();
    }
}
