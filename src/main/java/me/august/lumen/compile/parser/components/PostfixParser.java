package me.august.lumen.compile.parser.components;

import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.parser.ast.expr.ArrayAccessExpr;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.ast.expr.IncrementExpr;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.Type;

// not really infix, but we can pretend to be
// one and just not parse a right-hand expression.
public class PostfixParser implements InfixParser {

    @Override
    public Expression parse(TokenParser parser, Expression left, Token token) {
        switch (token.getType()) {
            case INC:
                return new IncrementExpr(left, IncrementExpr.Op.INC, true);

            case DEC:
                return new IncrementExpr(left, IncrementExpr.Op.DEC, true);

            case L_BRACKET:
                Expression index = parser.parseExpression();
                parser.expect(Type.R_BRACKET);
                left = new ArrayAccessExpr(left, index);

                while (parser.accept(Type.L_BRACKET)) {
                    index = parser.parseExpression();
                    parser.expect(Type.R_BRACKET);
                    left = new ArrayAccessExpr(left, index);
                }
                return left;

            default:
                throw new RuntimeException("Unexpected token: " + token);
        }
    }

    @Override
    public int getPrecedence() {
        return Precedence.POSTFIX.getLevel();
    }
}
