package me.august.lumen.compile.parser.ast.expr.owned;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.expr.Expression;
import org.objectweb.asm.MethodVisitor;

public interface OwnedExpr extends Expression {

    default Expression getOwner() {
        return null;
    }

    default Expression getTail() {
        Expression expr = getOwner();
        if (expr == null) return this;

        while (expr instanceof OwnedExpr) {
            expr = ((OwnedExpr) expr).getTail();
        }

        return expr;
    }

    @Override
    default void generate(MethodVisitor visitor, BuildContext context) {
        if (getOwner() != null) getOwner().generate(visitor, context);
    }
}
