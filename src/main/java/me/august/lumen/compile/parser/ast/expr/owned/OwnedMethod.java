package me.august.lumen.compile.parser.ast.expr.owned;

import me.august.lumen.compile.parser.ast.expr.Expression;

import java.util.List;

public class OwnedMethod implements OwnedExpr {

    private String name;
    private List<Expression> params;
    private OwnedExpr owner;

    public OwnedMethod(String name, List<Expression> params, OwnedExpr owner) {
        this.name = name;
        this.params = params;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public List<Expression> getParams() {
        return params;
    }

    public OwnedExpr getOwner() {
        return owner;
    }
}
