package me.august.lumen.compile.resolve;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.ast.ClassNode;
import me.august.lumen.compile.ast.FieldNode;
import me.august.lumen.compile.ast.MethodNode;
import me.august.lumen.compile.ast.TypedNode;
import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.ast.stmt.VarStmt;
import me.august.lumen.compile.resolve.type.BasicType;
import org.objectweb.asm.Type;

/**
 * A common visitor pattern for all AST nodes associated with a type.
 */
public abstract class TypeVisitor implements ASTVisitor {

    protected abstract Type resolveType(BasicType unresolved);

    public void visitType(TypedNode type) {
        Type resolved = resolveType(type.getUnresolvedType());
        type.setResolvedType(resolved);
    }

    @Override
    public void visitVar(VarStmt var) {
        visitType(var);
    }

    @Override
    public void visitField(FieldNode field) {
        visitType(field);
    }

    @Override
    public void visitMethod(MethodNode method) {
        visitType(method);

        method.getParameters().forEach(this::visitType);
    }

    @Override
    public void visitExpression(Expression expr) {
        if (expr instanceof TypedNode) {
            TypedNode typedNode = (TypedNode) expr;
            if (!typedNode.isResolved())
                visitType((TypedNode) expr);
        }
    }

    @Override
    public void visitClass(ClassNode cls) {
        if (!cls.getSuperClass().isResolved()) {
            visitType(cls.getSuperClass());
        }
    }
}
