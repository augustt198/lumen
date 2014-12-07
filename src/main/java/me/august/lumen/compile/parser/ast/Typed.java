package me.august.lumen.compile.parser.ast;

public class Typed {

    protected String type;
    protected String resolvedType;

    public Typed(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getResolvedType() {
        return resolvedType;
    }

    public boolean isResolved() {
        return resolvedType != null;
    }

    public void setResolvedType(String resolvedType) {
        this.resolvedType = resolvedType;
    }
}
