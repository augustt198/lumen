package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.analyze.ASTVisitor;
import me.august.lumen.analyze.VisitorConsumer;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.scanner.Op;
import org.objectweb.asm.MethodVisitor;

public interface Expression extends CodeBlock, VisitorConsumer {

    Expression[] getChildren();
    Op getOp();
    boolean isConstant();

    default void generate(MethodVisitor visitor, BuildContext context) {
        throw new UnsupportedOperationException();
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

}
