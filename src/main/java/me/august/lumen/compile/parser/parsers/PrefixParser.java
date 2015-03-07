package me.august.lumen.compile.parser.parsers;

import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.scanner.Token;

public interface PrefixParser {

    Expression parse(TokenParser parser, Token token);

    default void unexpectedToken(Token token) {
        throw new RuntimeException("Unexpected token: " + token);
    }

}
