package me.august.lumen.compile.parser.parsers;

import me.august.lumen.compile.ast.expr.CastExpr;
import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.resolve.type.BasicType;
import me.august.lumen.compile.scanner.Token;

public class CastParser implements InfixParser {

    @Override
    public Expression parse(TokenParser parser, Expression left, Token token) {
        BasicType type = parser.nextUnresolvedType();

        return new CastExpr(left, type);
    }

    @Override
    public int getPrecedence() {
        return Precedence.CAST.getLevel();
    }
}


