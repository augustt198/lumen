package me.august.lumen.compile.resolve;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.parser.Parser;
import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.FieldNode;
import me.august.lumen.compile.parser.ast.Typed;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.ast.expr.MethodNode;
import me.august.lumen.compile.parser.ast.stmt.VarStmt;
import org.objectweb.asm.Type;

import java.util.Map;

/**
 * A common visitor pattern for all AST nodes associated with a type.
 */
public abstract class TypeVisitor implements ASTVisitor {

    protected abstract Type resolveType(String simple);

    public void visitType(Typed type) {
        Type resolved = resolveType(type.getSimpleType());
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

        for (Map.Entry<String, String> entry : method.getParameters().entrySet()) {
            String resolved = resolveType(entry.getValue()).getClassName();
            method.getParameters().put(entry.getKey(), resolved);
        }
    }

    @Override
    public void visitExpression(Expression expr) {
        if (expr instanceof Typed) {
            visitType((Typed) expr);
        }
    }

    @Override
    public void visitClass(ClassNode cls) {
        // TODO better work-around
        if (!cls.getSuperClass().equals(Parser.OBJECT_CLASS_INTERNAL_NAME)) {
            String className = resolveType(cls.getSuperClass()).getClassName();
            cls.setSuperClass(className);
        }
    }
}
