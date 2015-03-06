package me.august.lumen.compile.parser.parsers;

import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.ast.expr.*;
import me.august.lumen.compile.scanner.Token;

public class UnaryPrefixParser implements PrefixParser {

    @Override
    public Expression parse(TokenParser parser, Token token) {
        Expression operand = parser.parseExpression();
        switch (token.getType()) {
            // Unary plus, do nothing
            case PLUS:
                return operand;

            case MIN:
                return new UnaryMinusExpr(operand);

            case BIT_COMP:
                return new BitwiseComplementExpr(operand);

            // increment & decrement
            case INC:
                return new IncrementExpr(operand, IncrementExpr.Op.INC, false);
            case DEC:
                return new IncrementExpr(operand, IncrementExpr.Op.DEC, false);

            // logical not
            case NOT:
                return new NotExpr(operand);

            default:
                throw new RuntimeException("Unexpected token: " + token);
        }
    }
}
