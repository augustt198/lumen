package me.august.lumen.compile.resolve;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.ast.CodeBlock;
import me.august.lumen.compile.ast.stmt.Body;

/**
 * Marks all statements/expressions in a Body
 * as top level statements.
 */
public class TopLevelStatementMarker implements ASTVisitor {

    @Override
    public void visitBody(Body body) {
        for (CodeBlock code : body.getChildren()) {
            code.markAsTopLevelStatement(true);
        }
    }
}
