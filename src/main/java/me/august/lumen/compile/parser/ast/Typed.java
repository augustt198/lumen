package me.august.lumen.compile.parser.ast;

import me.august.lumen.compile.resolve.type.UnresolvedType;
import org.objectweb.asm.Type;

public class Typed {

    protected UnresolvedType unresolvedType;
    protected Type resolvedType;

    public Typed(UnresolvedType unresolvedType) {
        this.unresolvedType = unresolvedType;
    }

    public UnresolvedType getUnresolvedType() {
        return unresolvedType;
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
