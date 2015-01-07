package me.august.lumen.compile.analyze.scope;

public class ClassScope extends Scope {

    private String className;

    public ClassScope(String className) {
        super(null, ScopeType.CLASS);
    }

    public String getClassName() {
        return className;
    }
}
