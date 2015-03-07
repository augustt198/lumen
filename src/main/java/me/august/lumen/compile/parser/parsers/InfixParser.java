package me.august.lumen.compile.parser.parsers;

import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.scanner.Token;

public interface InfixParser {

    Expression parse(TokenParser parser, Expression left, Token token);

    int getPrecedence();

}
