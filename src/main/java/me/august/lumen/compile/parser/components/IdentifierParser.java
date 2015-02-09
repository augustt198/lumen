package me.august.lumen.compile.parser.components;

import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.ast.expr.IdentExpr;
import me.august.lumen.compile.scanner.Token;

public class IdentifierParser implements PrefixParser {

    @Override
    public Expression parse(TokenParser parser, Token token) {
        return new IdentExpr(token.getContent());
    }
}
