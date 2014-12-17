package me.august.lumen.compile.parser.ast.expr.owned;

import me.august.lumen.compile.parser.ast.expr.Expression;

public interface OwnedExpr extends Expression {

    default Expression getOwner() {
        return null;
    }

}
