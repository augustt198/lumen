package me.august.lumen.compile.parser.components;

import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.ast.expr.NumExpr;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.Type;
import me.august.lumen.compile.scanner.tokens.NumberToken;

public class NumberParser implements PrefixParser {

    @Override
    public Expression parse(TokenParser parser, Token token) {
        if (token.getType() == Type.NUMBER) {
            NumberToken numTok = (NumberToken) token;
            return new NumExpr(numTok.getValue());
        } else {
            throw new RuntimeException("Unexpected token: " + token);
        }
    }
}
