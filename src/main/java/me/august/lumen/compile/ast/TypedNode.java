package me.august.lumen.compile.ast;

import me.august.lumen.compile.resolve.type.BasicType;
import org.objectweb.asm.Type;

public class TypedNode {

    protected BasicType unresolvedType;
    protected Type resolvedType;

    public TypedNode(BasicType unresolvedType) {
        this.unresolvedType = unresolvedType;
    }

    public BasicType getUnresolvedType() {
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
