package me.august.lumen.compile.ast;

import me.august.lumen.compile.resolve.type.UnresolvedType;
import org.objectweb.asm.Type;

public class TypedNode {

    protected UnresolvedType unresolvedType;
    protected Type resolvedType;

    public TypedNode(UnresolvedType unresolvedType) {
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
