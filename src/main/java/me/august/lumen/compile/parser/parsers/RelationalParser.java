package me.august.lumen.compile.parser.parsers;

import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.ast.expr.InstanceofExpr;
import me.august.lumen.compile.ast.expr.NotExpr;
import me.august.lumen.compile.ast.expr.RelExpr;
import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.TokenType;

public class RelationalParser implements InfixParser {

    @Override
    public Expression parse(TokenParser parser, Expression left, Token token) {
        switch (token.getType()) {
            case INSTANCEOF_KEYWORD:
                UnresolvedType type = parser.nextUnresolvedType();
                return new InstanceofExpr(left, type);
            case NOT_INSTANCEOF_KEYWORD:
                type = parser.nextUnresolvedType();
                return new NotExpr(new InstanceofExpr(left, type));
            default:
                Expression right = parser.parseExpression(getPrecedence());
                RelExpr.Op operator = getOperator(token.getType());
                return new RelExpr(left, right, operator);
        }
    }

    @Override
    public int getPrecedence() {
        return Precedence.RELATIONAL.getLevel();
    }

    private RelExpr.Op getOperator(TokenType type) {
        switch (type) {
            case GT:  return RelExpr.Op.GT;
            case GTE: return RelExpr.Op.GTE;
            case LT:  return RelExpr.Op.LT;
            case LTE: return RelExpr.Op.LTE;
            default: throw new RuntimeException("Unexpected type: " + type);
        }
    }
}
