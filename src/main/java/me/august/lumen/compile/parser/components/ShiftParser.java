package me.august.lumen.compile.parser.components;

import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.ast.expr.ShiftExpr;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.TokenType;

public class ShiftParser implements InfixParser {

    @Override
    public Expression parse(TokenParser parser, Expression left, Token token) {
        Expression right = parser.parseExpression(getPrecedence());

        return new ShiftExpr(left, right, getOperation(token.getType()));
    }

    @Override
    public int getPrecedence() {
        return Precedence.SHIFT.getLevel();
    }

    private ShiftExpr.Op getOperation(TokenType type) {
        switch (type) {
            case SH_L: return ShiftExpr.Op.SH_L;
            case SH_R: return ShiftExpr.Op.SH_R;
            case U_SH_R: return ShiftExpr.Op.U_SH_R;
            default: throw new RuntimeException("Unexpected type: " + type);
        }
    }
}
