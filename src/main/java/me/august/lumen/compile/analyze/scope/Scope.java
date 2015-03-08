package me.august.lumen.compile.analyze.scope;

import me.august.lumen.compile.analyze.var.LocalVariable;
import me.august.lumen.compile.analyze.var.VariableReference;

import java.util.LinkedHashMap;
import java.util.Map;

public class Scope {

    private Scope parent;

    private Map<String, VariableReference> variableTable = new LinkedHashMap<>();
    private ScopeType type;

    public Scope(Scope parent) {
        this(parent, ScopeType.NORMAL);
    }

    public Scope(Scope parent, ScopeType type) {
        this.parent = parent;
        this.type   = type;
    }

    public VariableReference getVariable(String name) {
        VariableReference ref = variableTable.get(name);
        // variable is in this scope's table, return it
        if (ref != null) return ref;

        // if we have a parent, try to get the
        // variable from them
        if (parent != null) {
            return parent.getVariable(name);

        // we've reached the root scope, variable doesn't exist
        } else {
            return null;
        }
    }

    public void setVariable(String name, VariableReference ref) {
        variableTable.put(name, ref);
    }

    public ScopeType getType() {
        return type;
    }

    public Scope getRoot() {
        Scope scope = this;
        while (true) {
            if (scope.parent == null) {
                return scope;
            } else {
                scope = scope.parent;
            }
        }
    }

    public Scope fromType(ScopeType type) {
        Scope scope = this;
        while (scope != null) {
            if (scope.type == type) {
                return scope;
            } else {
                scope = scope.parent;
            }
        }

        return null;
    }

    public int nextLocalVariableIndex(boolean staticContext) {
        int idx = staticContext ? -1 : 0;

        int myMax = myMaxLocalVariableIndex();
        // we have a max local variable index in this
        // scope, return the next index (+1)
        if (myMax > -1) {
            return myMax + 1;
        }

        // check our parent scopes for a max
        Scope scope = this.parent;
        while (scope != null) {
            myMax = scope.myMaxLocalVariableIndex();
            if (myMax > -1) {
                return myMax + 1;
            }

            scope = scope.parent;
        }

        // no local variables found anywhere:
        // if static, return -1 + 1 = 0
        // if local,  return  0 + 1 = 1 (local variable 0 is `this`)
        return idx + 1;
    }

    private int myMaxLocalVariableIndex() {
        int max = -1;

        for (Map.Entry<String, VariableReference> entry : variableTable.entrySet()) {
            if (entry.getValue() instanceof LocalVariable) {
                LocalVariable localVar = (LocalVariable) entry.getValue();
                if (localVar.getIndex() > max) {
                    max = localVar.getIndex();
                }
            }
        }

        return max;
    }

    public Scope getParent() {
        return parent;
    }

    public void setParent(Scope parent) {
        this.parent = parent;
    }

    public Map<String, VariableReference> getVariableTable() {
        return variableTable;
    }
}
