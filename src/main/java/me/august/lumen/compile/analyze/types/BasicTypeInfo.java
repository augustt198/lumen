package me.august.lumen.compile.analyze.types;

import me.august.lumen.compile.resolve.TypeResolver;
import me.august.lumen.compile.resolve.type.BasicType;
import org.objectweb.asm.Type;

public class BasicTypeInfo implements TypeInfo {

    private BasicType unresolved;
    private Type resolved;

    public BasicTypeInfo(String unresolved) {
        this.unresolved = new BasicType(unresolved);
    }

    public BasicTypeInfo(BasicType unresolved) {
        this.unresolved = unresolved;
    }

    @Override
    public void resolve(TypeResolver resolver) {
        resolved = resolver.resolveType(unresolved);
    }

    public Type getResolvedType() {
        return resolved;
    }

    public BasicType getUnresolvedType() {
        return unresolved;
    }
}
