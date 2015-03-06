package me.august.lumen.compile.analyze;

import java.util.function.Consumer;

/**
 * Interface for visiting an AST from the top down
 * and from the bottom up.
 */
public interface VisitorConsumer extends Consumer<ASTVisitor> {

    /**
     * Traverses from the top down, meaning a
     * a node is visited before its children
     *
     * @param visitor The visitor to accept
     */
    void acceptTopDown(ASTVisitor visitor);

    /**
     * Traverses from the bottom up, meaning
     * a node's children is visited before the
     * node itself.
     *
     * @param visitor The visitor to accept
     */
    void acceptBottomUp(ASTVisitor visitor);

}
