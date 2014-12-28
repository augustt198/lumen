package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;

public interface OwnedExpr extends Expression {

    default Expression getOwner() {
        return null;
    }

    default Expression getTail() {
        Expression expr = getOwner();
        if (expr == null) return this;

        while (expr instanceof OwnedExpr && ((OwnedExpr) expr).getOwner() != null) {
            expr = ((OwnedExpr) expr).getTail();
        }

        return expr;
    }

    @Override
    default void generate(MethodVisitor visitor, BuildContext context) {
        if (getOwner() != null) getOwner().generate(visitor, context);
    }
}
