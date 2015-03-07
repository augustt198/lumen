package me.august.lumen.compile.parser.parsers;

import me.august.lumen.compile.ast.expr.AssignmentExpr;
import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.ast.expr.VariableExpression;
import me.august.lumen.compile.parser.TokenParser;
import me.august.lumen.compile.scanner.Token;

public class AssignmentParser implements InfixParser {

    @Override
    public Expression parse(TokenParser parser, Expression left, Token token) {
        if (!(left instanceof VariableExpression)) {
            throw new RuntimeException("Expected variable");
        }

        Expression right = parser.parseExpression(getPrecedence());

        return new AssignmentExpr((VariableExpression) left, right);
    }

    @Override
    public int getPrecedence() {
        return Precedence.ASSIGNMENT.getLevel();
    }
}
