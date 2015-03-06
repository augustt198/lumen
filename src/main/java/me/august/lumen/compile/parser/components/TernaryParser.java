package me.august.lumen.compile.parser.components;

import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.ast.expr.TernaryExpr;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.TokenType;

public class TernaryParser implements InfixParser {

    @Override
    public Expression parse(TokenParser parser, Expression left, Token token) {
        Expression ifBranch = parser.parseExpression(getPrecedence());

        parser.expect(TokenType.COLON);

        Expression elseBranch = parser.parseExpression(getPrecedence());

        return new TernaryExpr(left, ifBranch, elseBranch);
    }

    @Override
    public int getPrecedence() {
        return Precedence.TERNARY.getLevel();
    }
}
