package me.august.lumen.compile.resolve;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.parser.ast.FieldNode;
import me.august.lumen.compile.parser.ast.Typed;
import me.august.lumen.compile.parser.ast.code.VarDeclaration;
import me.august.lumen.compile.parser.ast.expr.MethodNode;

import java.util.Map;

/**
 * A common visitor pattern for all AST nodes associated with a type.
 */
public abstract class TypeVisitor extends ASTVisitor {

    protected abstract String resolveType(String simple);

    public void visitType(Typed type) {
        type.setResolvedType(type.getType());
    }

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

        for (Map.Entry<String, String> entry : method.getParameters().entrySet()) {
            String resolved = resolveType(entry.getValue());
            method.getParameters().put(entry.getKey(), resolved);
        }
    }

}
