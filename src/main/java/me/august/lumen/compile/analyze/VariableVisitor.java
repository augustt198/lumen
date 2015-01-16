package me.august.lumen.compile.analyze;

import me.august.lumen.compile.analyze.scope.*;
import me.august.lumen.compile.analyze.var.ClassVariable;
import me.august.lumen.compile.analyze.var.LocalVariable;
import me.august.lumen.compile.analyze.var.VariableReference;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.FieldNode;
import me.august.lumen.compile.parser.ast.MethodNode;
import me.august.lumen.compile.parser.ast.Parameter;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.ast.expr.IdentExpr;
import me.august.lumen.compile.parser.ast.expr.OwnedExpr;
import me.august.lumen.compile.parser.ast.stmt.*;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VariableVisitor implements ASTVisitor {

    private Scope scope;
    private boolean skipNext;

    // name-variable pairs that have yet to be added
    // to a scope. Currently used for adding method
    // parameters to the method body that follows.
    Map<String, VariableReference> pending = new HashMap<>();
    BuildContext build;
    String className;

    public VariableVisitor(BuildContext build) {
        this.build = build;
    }

    @Override
    public void visitClass(ClassNode cls) {
        pushScope(new ClassScope(cls.getName()));
        className = cls.getName();
    }

    @Override
    public void visitField(FieldNode field) {
        Type type = field.getResolvedType();
        scope.setVariable(field.getName(), new ClassVariable(
            className,
            field.getName(),
            type
        ));
    }

    @Override
    public void visitMethod(MethodNode method) {
        pushScope(new MethodScope(this.scope, method));

        // local variable index
        int idx = 0;
        for (Parameter param : method.getParameters()) {
            idx++;
            Type type = param.getResolvedType();
            scope.setVariable(param.getName(), new LocalVariable(idx, type));
        }

        // when the method body is visited, skip it.
        // we already created the scope for the body here.
        skipNext();
    }

    @Override
    public void visitBody(Body body) {
        pushScope(new Scope(this.scope));

        // wrap keyset in new ArrayList to prevent
        // ConcurrentModificationException
        for (String name : new ArrayList<>(pending.keySet())) {
            scope.setVariable(name, pending.remove(name));
        }
    }

    @Override
    public void visitBodyEnd(Body body) {
        popScope();
    }

    @Override
    public void visitClassEnd(ClassNode cls) {
        popScope();
    }

    @Override
    public void visitWhileStmt(WhileStmt stmt) {
        pushScope(new LoopScope(scope, stmt));
    }

    @Override
    public void visitBreakStmt(BreakStmt stmt) {
        LoopScope loopScope = nextLoop();

        if (loopScope == null)
            throw new RuntimeException("break used where loop not found");

        stmt.setOwner(loopScope.getLoop());
    }

    @Override
    public void visitNextStmt(NextStmt stmt) {
        LoopScope loopScope = nextLoop();

        if (loopScope == null)
            throw new RuntimeException("break used where loop not found");

        stmt.setOwner(loopScope.getLoop());
    }

    @Override
    public void visitVar(VarStmt var) {
        Type type = var.getResolvedType();
        LocalVariable ref = new LocalVariable(nextLocalIndex(), type);
        scope.setVariable(var.getName(), ref);
        var.setRef(ref);
    }

    @Override
    public void visitExpression(Expression expr) {
        if (expr instanceof OwnedExpr) {
            Expression tail = ((OwnedExpr) expr).getTail();
            if (tail instanceof IdentExpr) {
                handleIdent((IdentExpr) tail);
            }
        }
    }

    private void handleIdent(IdentExpr expr) {
        VariableReference var = scope.getVariable(expr.getIdentifier());

        if (var == null)
            throw new RuntimeException("Undefined variable: " + expr.getIdentifier());

        expr.setVariableReference(var);
        expr.setExpressionType(var.getType());
    }

    /**
     * Gets the next local variable index starting
     * at a specific Scope
     * @return The next local variable index
     */
    public int nextLocalIndex() {
        Scope s = this.scope;

        int max = -1;

        while (s != null) {
            for (VariableReference vr : s.getVariableTable().values()) {
                if (vr instanceof LocalVariable) {
                    LocalVariable local = (LocalVariable) vr;
                    if (local.getIndex() > max)
                        max = local.getIndex();
                }
            }
            s = s.getParent();
        }

        if (max == -1) {
            MethodScope methodScope = (MethodScope) scope.fromType(ScopeType.METHOD);
            return methodScope.getMethod().isStatic() ? 0 : 1;
        } else {
            return max + 1;
        }

    }

    private void skipNext() {
        this.skipNext = true;
    }

    private Scope pushScope(Scope scope) {
        if (skipNext) {
            skipNext = false;
            return null;
        } else {
            if (scope.getParent() == null)
                scope.setParent(this.scope);
            return this.scope = scope;
        }
    }

    private void popScope() {
        if (scope.getParent() == null) {
            scope = null;
        } else {
            scope = scope.getParent();
        }
    }

    private LoopScope nextLoop() {
        return (LoopScope) scope.fromType(ScopeType.LOOP);
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }
}
