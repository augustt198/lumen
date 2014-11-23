package me.august.lumen.compile.parser.ast.expr;

import java.util.List;

public class MethodCallExpr extends TerminalExpression {

    private String identifier;
    private List<Expression> params;

    public MethodCallExpr(String identifier, List<Expression> params) {
        this.identifier = identifier;
        this.params = params;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public String toString() {
        return "MethodCallExpr{" +
            "identifier='" + identifier + '\'' +
            ", params=" + params +
            '}';
    }
}
