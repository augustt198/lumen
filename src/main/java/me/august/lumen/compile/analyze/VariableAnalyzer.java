package me.august.lumen.compile.analyze;

import me.august.lumen.compile.analyze.scope.*;
import me.august.lumen.compile.analyze.types.BasicTypeInfo;
import me.august.lumen.compile.analyze.var.ClassVariable;
import me.august.lumen.compile.analyze.var.LocalVariable;
import me.august.lumen.compile.analyze.var.VariableReference;
import me.august.lumen.compile.ast.ClassNode;
import me.august.lumen.compile.ast.FieldNode;
import me.august.lumen.compile.ast.MethodNode;
import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.ast.expr.IdentExpr;
import me.august.lumen.compile.ast.stmt.*;
import me.august.lumen.compile.codegen.BuildContext;

import java.util.LinkedHashMap;
import java.util.Map;

public class VariableAnalyzer implements ASTVisitor {

    private Scope scope;

    private Map<Expression, VariableReference> variableLookup
            = new LinkedHashMap<>();

    private TypeAnnotator types;

    private BuildContext context;

    public VariableAnalyzer(TypeAnnotator types, BuildContext context) {
        this.types   = types;
        this.context = context;
    }

    private void popScope() {
        if (scope != null) {
            scope = scope.getParent();
        }
    }

    @Override
    public void visitClass(ClassNode cls) {
        scope = new ClassScope(cls.getName());

        for (FieldNode field : cls.getFields()) {
            BasicTypeInfo typeInfo = (BasicTypeInfo) types.getValue(field);

            ClassVariable fieldRef = new ClassVariable(
                    cls.getName(), field.getName(), typeInfo.getResolvedType()
            );
            scope.setVariable(field.getName(), fieldRef);
        }
    }

    @Override
    public void visitClassEnd(ClassNode cls) {
        popScope();
    }

    @Override
    public void visitEachStmt(EachStmt stmt) {
        pushLoopScope(stmt);
    }

    @Override
    public void visitForStmt(ForStmt stmt) {
        pushLoopScope(stmt);
    }

    @Override
    public void visitWhileStmt(WhileStmt stmt) {
        pushLoopScope(stmt);
    }

    private void pushLoopScope(Loop loop) {
        scope = new LoopScope(scope, loop);
    }

    @Override
    public void visitBodyEnd(Body body) {
        popScope();
    }

    @Override
    public void visitExpression(Expression expr) {
        if (expr instanceof IdentExpr) {
            IdentExpr ident = (IdentExpr) expr;

            // Ignore identifiers such as:
            // foo.bar
            //     ^^^ NOT a local variable
            if (ident.getOwner() == null) {
                visitIdentifier(ident);
            }
        }
    }

    private void visitIdentifier(IdentExpr ident) {
        VariableReference variable = scope.getVariable(ident.getIdentifier());

        if (variable == null) {
            context.error(
                    "Undefined variable \"" + ident.getIdentifier() + "\"",
                    true, ident
            );
        }

        ident.setVariableReference(variable);

        variableLookup.put(ident, variable);
    }

    @Override
    public void visitVar(VarStmt var) {
        // local variable declared, add it to the
        // scope's variable table

        int nextIndex = scope.nextLocalVariableIndex(isStaticMethod());

        BasicTypeInfo typeInfo = (BasicTypeInfo) types.getValue(var);

        LocalVariable localVar = new LocalVariable(
                nextIndex, typeInfo.getResolvedType()
        );

        scope.setVariable(var.getName(), localVar);
    }

    @Override
    public void visitMethod(MethodNode method) {
        scope = new MethodScope(scope, method);
    }

    @Override
    public void visitMethodEnd(MethodNode method) {
        popScope();
    }

    private boolean isStaticMethod() {
        if (scope == null) {
            return false;
        }

        MethodScope methodScope = (MethodScope) scope.fromType(ScopeType.METHOD);
        return methodScope.getMethod().isStatic();
    }

    public VariableReference getVariableReference(Expression expr) {
        return variableLookup.get(expr);
    }

    public Map<Expression, VariableReference> getVariableLookup() {
        return variableLookup;
    }
}
