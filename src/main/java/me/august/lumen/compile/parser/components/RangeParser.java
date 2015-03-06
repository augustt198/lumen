package me.august.lumen.compile.parser.components;

import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.parser.ast.expr.ArrayInitializerExpr;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.ast.expr.IdentExpr;
import me.august.lumen.compile.parser.ast.expr.RangeExpr;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.TokenType;

import java.util.ArrayList;
import java.util.List;

import static me.august.lumen.compile.scanner.TokenType.*;

public class RangeParser implements InfixParser {

    @Override
    public Expression parse(TokenParser parser, Expression left, Token token) {
        boolean inclusive = token.getType() == TokenType.RANGE_INCLUSIVE;

        if (parser.accept(TokenType.L_BRACKET)) {
            return parseArrayInitializer(parser, left);
        } else {
            Expression right = parser.parseExpression(getPrecedence());
            return new RangeExpr(left, right, inclusive);
        }
    }

    private ArrayInitializerExpr parseArrayInitializer(TokenParser parser, Expression left) {
        if (!(left instanceof IdentExpr)) {
            throw new RuntimeException("Expected identifier");
        }

        IdentExpr ident = (IdentExpr) left;
        UnresolvedType type = new UnresolvedType(ident.getIdentifier());

        List<Expression> lengths = new ArrayList<>();
        int dims = 1;

        lengths.add(parser.parseExpression());
        parser.expect(R_BRACKET);

        boolean unknown = false;
        while (parser.accept(L_BRACKET)) {
            if (parser.accept(QUESTION)) {
                unknown = true;
                dims++;
            } else {
                if (unknown) {
                    throw new RuntimeException("Illegal array initialization");
                }
                lengths.add(parser.parseExpression());
                dims++;
            }
            parser.expect(R_BRACKET);
        }

        return new ArrayInitializerExpr(type, lengths, dims);
    }

    @Override
    public int getPrecedence() {
        return Precedence.RANGE.getLevel();
    }
}
