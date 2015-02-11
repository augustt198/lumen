package me.august.lumen.compile.parser.components;

import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.parser.ast.expr.*;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.Type;

import java.util.ArrayList;
import java.util.List;

public class IdentifierParser implements PrefixParser {

    @Override
    public Expression parse(TokenParser parser, Token token) {
        Expression expression;
        String identifier = token.getContent();

        // Hit separator, must be static field or method call
        if (parser.accept(Type.SEP)) {
            String name = parser.consume().expectType(Type.IDENTIFIER).getContent();
            UnresolvedType classType = new UnresolvedType(identifier);

            // method -> Foo::bar()
            if (parser.peek().getType() == Type.L_PAREN) {
                List<Expression> parameters = parseParameters(parser);
                expression = new StaticMethodCall(classType, name, parameters);

            // field -> Foo::bar
            } else {
                expression = new StaticField(classType, name);
            }
        } else {
            if (parser.peek().getType() == Type.L_PAREN) {
                List<Expression> parameters = parseParameters(parser);
                expression = new MethodCallExpr(identifier, parameters);
            } else {
                expression = new IdentExpr(identifier);
            }
        }

        while (parser.accept(Type.DOT)) {
            String name = parser.consume().expectType(Type.IDENTIFIER).getContent();

            // instance method call
            if (parser.peek().getType() == Type.L_PAREN) {
                List<Expression> parameters = parseParameters(parser);
                expression = new MethodCallExpr(name, parameters, expression);

            // instance field
            } else {
                expression = new IdentExpr(name, expression);
            }
        }

        return expression;
    }

    private List<Expression> parseParameters(TokenParser parser) {
        parser.expect(Type.L_PAREN);

        List<Expression> parameters = new ArrayList<>();
        while (parser.peek().getType() != Type.R_PAREN) {
            parameters.add(parser.parseExpression());

            if (!parser.accept(Type.COMMA)) {
                break;
            }
        }
        parser.expect(Type.R_PAREN);

        return parameters;
    }
}
