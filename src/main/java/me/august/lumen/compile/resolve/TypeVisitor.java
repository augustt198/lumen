package me.august.lumen.compile.resolve;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.parser.ast.FieldNode;
import me.august.lumen.compile.parser.ast.Typed;
import me.august.lumen.compile.parser.ast.code.VarDeclaration;
import me.august.lumen.compile.parser.ast.expr.MethodNode;

/**
 * A common visitor pattern for all AST nodes associated with a type.
 */
public abstract class TypeVisitor extends ASTVisitor {

    protected abstract void visitType(Typed node);

    @Override
    public void visitVar(VarDeclaration var) {
        visitType(var);
    }

    @Override
    public void visitField(FieldNode field) {
        visitType(field);
    }

    @Override
    public void visitMethod(MethodNode method) {
        visitType(method);
    }

}
