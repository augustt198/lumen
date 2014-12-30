package me.august.lumen.compile.resolve;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.parser.ast.Popable;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.ast.stmt.Body;
import org.objectweb.asm.Type;

/**
 * Visits method calls in a Body, and adds the
 * POP instruction after the method call if the
 * method return type is not void.
 */
public class PopInstructionVisitor implements ASTVisitor {

    @Override
    public void visitBody(Body body) {
        for (CodeBlock code : body.getChildren()) {
            if (code instanceof Expression && code instanceof Popable) {
                Expression expr = (Expression) code;
                Popable popable = (Popable)    expr;

                Type type = expr.expressionType();
                if (type != null && type != Type.VOID_TYPE) {
                    popable.shouldPop(true);
                }
            }
        }
    }
}
