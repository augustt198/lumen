package me.august.lumen.compile.ast.expr;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.ast.CodeBlock;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public interface Expression extends CodeBlock, VisitorConsumer {

    default boolean isConstant() {
        return false;
    }

    default void generate(MethodVisitor visitor, BuildContext context) {
        throw new UnsupportedOperationException();
    }

    // for evaluating constant expressions
    default void evaluate() {
        if (!isConstant()) throw new UnsupportedOperationException("This expression is not constant");
    }

    default void accept(ASTVisitor visitor) {
        if (getChildren() != null) {
            for (Expression child : getChildren()) {
                if (child == null) continue;
                child.accept(visitor);
            }
        }

        if (this instanceof OwnedExpr) {
            Expression owned = ((OwnedExpr) this).getOwner();
            if (owned != null)
                owned.accept(visitor);
        }
        visitor.visitExpression(this);
    }

    default void acceptTopDown(ASTVisitor visitor) {
        visitor.visitExpression(this);

        for (Expression child : getChildren()) {
            child.acceptTopDown(visitor);
        }

        if (this instanceof OwnedExpr) {
            Expression owned = ((OwnedExpr) this).getOwner();
            owned.acceptTopDown(visitor);
        }
    }

    default void acceptBottomUp(ASTVisitor visitor) {
        for (Expression child : getChildren()) {
            child.acceptBottomUp(visitor);
        }

        if (this instanceof OwnedExpr) {
            Expression owned = ((OwnedExpr) this).getOwner();
            owned.acceptBottomUp(visitor);
        }

        visitor.visitExpression(this);
    }

    default Expression[] getChildren() {
        return new Expression[]{};
    }

    default Type expressionType() {
        return null;
    }

}
