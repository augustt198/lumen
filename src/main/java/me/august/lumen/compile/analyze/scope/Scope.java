package me.august.lumen.compile.analyze.scope;

import me.august.lumen.compile.analyze.var.VariableReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scope {

    private Scope parent;

    private Map<String, VariableReference> variableTable = new HashMap<>();
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
        while (scope != null)
            scope = scope.parent;

        return scope;
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

        return scope;
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
