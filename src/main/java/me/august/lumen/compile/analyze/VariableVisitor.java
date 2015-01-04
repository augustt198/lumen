package me.august.lumen.compile.analyze;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.analyze.var.ClassVariable;
import me.august.lumen.compile.analyze.var.LocalVariable;
import me.august.lumen.compile.analyze.var.VariableReference;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.FieldNode;
import me.august.lumen.compile.parser.ast.Parameter;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.ast.expr.IdentExpr;
import me.august.lumen.compile.parser.ast.expr.MethodNode;
import me.august.lumen.compile.parser.ast.expr.OwnedExpr;
import me.august.lumen.compile.parser.ast.stmt.Body;
import me.august.lumen.compile.parser.ast.stmt.VarStmt;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class VariableVisitor implements ASTVisitor {
    public static class Scope {
        Map<String, VariableReference> vars = new HashMap<>();
        String className;

        public Scope(String className) {
            this.className = className;
        }

        public void addVariable(String name, VariableReference var) {
            vars.put(name, var);
        }
    }

    public Stack<Scope> scopes = new Stack<>();

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
        scopes.push(new Scope(cls.getName()));
        className = cls.getName();
    }

    @Override
    public void visitField(FieldNode field) {
        Scope scope = scopes.lastElement();

        Type type = field.getResolvedType();
        scope.addVariable(field.getName(), new ClassVariable(
            className,
            field.getName(),
            type
        ));
    }

    @Override
    public void visitMethod(MethodNode method) {
        // local variable index
        int idx = 0;
        for (Parameter param : method.getParameters()) {
            idx++;
            Type type = param.getResolvedType();
            pending.put(param.getName(), new LocalVariable(idx, type));
        }
    }

    @Override
    public void visitBody(Body body) {
        Scope scope = new Scope(className);
        scopes.push(scope);

        // wrap keyset in new ArrayList to prevent
        // ConcurrentModificationException
        for (String name : new ArrayList<>(pending.keySet())) {
            scope.addVariable(name, pending.remove(name));
        }
    }

    @Override
    public void visitBodyEnd(Body body) {
        scopes.pop();
    }

    @Override
    public void visitClassEnd(ClassNode cls) {
        scopes.pop();
    }

    @Override
    public void visitVar(VarStmt var) {
        Scope scope = scopes.lastElement();

        Type type = var.getResolvedType();
        LocalVariable ref = new LocalVariable(nextLocalIndex(), type);
        scope.addVariable(var.getName(), ref);
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
        VariableReference var = getVariable(expr.getIdentifier());
        expr.setVariableReference(var);
        expr.setExpressionType(var.getType());
    }

    /**
     * Gets the next local variable index
     * @return The next local variable index
     */
    public int nextLocalIndex() {
        return nextLocalIndex(scopes.size() - 1);
    }

    /**
     * Gets the next local variable index starting
     * at a specific Scope
     * @param stackPos The scope's index within the stack
     * @return The next local variable index
     */
    private int nextLocalIndex(int stackPos) {
        Scope scope = scopes.get(stackPos);

        int max = -1;

        for (VariableReference var : scope.vars.values()) {
            if (var instanceof LocalVariable) {
                LocalVariable local = (LocalVariable) var;
                if (local.getIndex() > max) max = local.getIndex();
            }
        }

        if (max > -1) return max + 1;

        if (max < 0 && stackPos > 0) {
            return nextLocalIndex(--stackPos);
        } else {
            return 1;
        }
    }

    public VariableReference getVariable(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            VariableReference var = scopes.get(i).vars.get(name);
            if (var != null) return var;
        }
        return null;
    }
}
