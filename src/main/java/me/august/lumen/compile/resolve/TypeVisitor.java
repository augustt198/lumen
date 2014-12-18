package me.august.lumen.compile.resolve;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.parser.ast.FieldNode;
import me.august.lumen.compile.parser.ast.Typed;
import me.august.lumen.compile.parser.ast.expr.StaticField;
import me.august.lumen.compile.parser.ast.expr.StaticMethodCall;
import me.august.lumen.compile.parser.ast.stmt.VarStmt;
import me.august.lumen.compile.parser.ast.expr.MethodNode;

import java.util.Map;

/**
 * A common visitor pattern for all AST nodes associated with a type.
 */
public abstract class TypeVisitor extends ASTVisitor {

    protected abstract String resolveType(String simple);

    public void visitType(Typed type) {
        type.setResolvedType(resolveType(type.getType()));
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
            String resolved = resolveType(entry.getValue());
            method.getParameters().put(entry.getKey(), resolved);
        }
    }

    @Override
    public void visitStaticField(StaticField sf) {
        visitType(sf);
    }

    @Override
    public void visitStaticMethodCall(StaticMethodCall smc) {
        visitType(smc);
    }
}
