package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.parser.ast.expr.owned.OwnedExpr;

import java.util.List;

public class MethodCallExpr extends TerminalExpression implements OwnedExpr {

    private String identifier;
    private List<Expression> params;

    private Expression owner;

    public MethodCallExpr(String identifier, List<Expression> params) {
        this(identifier, params, null);
    }

    public MethodCallExpr(String identifier, List<Expression> params, Expression owner) {
        this.identifier = identifier;
        this.params = params;
        this.owner = owner;
    }

    @Override
    public Expression getOwner() {
        return owner;
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
            ", owner=" + owner +
            '}';
    }
}
