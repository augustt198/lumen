package me.august.lumen.analyze;

import me.august.lumen.analyze.var.ClassVariable;
import me.august.lumen.analyze.var.LocalVariable;
import me.august.lumen.analyze.var.Variable;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.parser.ast.FieldNode;
import me.august.lumen.compile.parser.ast.code.Body;

import java.util.*;

public class LumenVisitor extends ASTVisitor {
    public static class Scope {
        Map<String, Variable> vars = new HashMap<>();
        String className;

        public Scope(String className) {
            this.className = className;
        }

        public void addVariable(String name, Variable var) {
            vars.put(name, var);
        }
    }

    Stack<Scope> scopes = new Stack<>();
    BuildContext build;
    private String className;

    public LumenVisitor(BuildContext build) {
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
        scope.addVariable(field.getName(), new ClassVariable(
            className,
            field.getName(),
            field.getType()
        ));
    }

    @Override
    public void visitBody(Body body) {
        scopes.push(new Scope(className));
    }

    @Override
    public void visitBodyEnd(Body body) {
        scopes.pop();
    }

    @Override
    public void visitClassEnd(ClassNode cls) {
        scopes.pop();
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

        for (Variable var : scope.vars.values()) {
            if (var instanceof LocalVariable) {
                LocalVariable local = (LocalVariable) var;
                if (local.getIndex() > max) max = local.getIndex();
            }
        }

        if (max > -1) return max + 1;

        if (max < 0 && stackPos >= 0) {
            return nextLocalIndex(--stackPos);
        } else {
            return 1;
        }
    }
}
