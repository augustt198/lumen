package me.august.lumen.compile.analyze.scope;

import me.august.lumen.compile.parser.ast.MethodNode;

public class MethodScope extends Scope {

    private MethodNode method;

    public MethodScope(Scope parent, MethodNode method) {
        super(parent, ScopeType.METHOD);
        this.method = method;

    }

    public MethodNode getMethod() {
        return method;
    }

}
