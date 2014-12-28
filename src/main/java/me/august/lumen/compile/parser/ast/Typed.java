package me.august.lumen.compile.parser.ast;

import org.objectweb.asm.Type;

public class Typed {

    protected String simpleType;
    protected Type resolvedType;

    public Typed(String simpleType) {
        this.simpleType = simpleType;
    }

    public String getSimpleType() {
        return simpleType;
    }

    public Type getResolvedType() {
        return resolvedType;
    }

    public boolean isResolved() {
        return resolvedType != null;
    }

    public void setResolvedType(Type resolvedType) {
        this.resolvedType = resolvedType;
    }
}
