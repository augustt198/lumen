package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.scanner.Op;
import org.objectweb.asm.MethodVisitor;

public interface Expression extends CodeBlock, VisitorConsumer {

    default Op getOp() {
        return null;
    }

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
        if (this instanceof IdentExpr) {
            visitor.visitIdentifier((IdentExpr) this);
            return;
        }

        Expression[] children = getChildren();
        if (children == null || children.length == 0) return;

        for (Expression child : children) {
            if (child == null) continue;
            if (child instanceof IdentExpr) {
                visitor.visitIdentifier((IdentExpr) child);
            } else {
                child.accept(visitor);
            }
        }
    }

    default Expression[] getChildren() {
        return new Expression[]{};
    }

}
