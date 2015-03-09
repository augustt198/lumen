package me.august.lumen.compile.analyze.types;

import me.august.lumen.compile.resolve.TypeResolver;
import me.august.lumen.compile.resolve.type.BasicType;
import org.objectweb.asm.Type;

public class ClassTypeInfo implements TypeInfo {

    private BasicType unresolvedSuper;
    private Type resolvedSuper;

    private BasicType[] unresolvedInterfaces;
    private Type[] resolvedInterfaces;

    public ClassTypeInfo(BasicType unresolvedSuper, BasicType... unresolvedInterfaces) {
        this.unresolvedSuper      = unresolvedSuper;
        this.unresolvedInterfaces = unresolvedInterfaces;
    }

    @Override
    public void resolve(TypeResolver resolver) {
        resolvedSuper = resolver.resolveType(unresolvedSuper);

        if (resolvedInterfaces == null) {
            resolvedInterfaces = new Type[unresolvedInterfaces.length];
        }
        for (int i = 0; i < unresolvedInterfaces.length; i++) {
            resolvedInterfaces[i] = resolver.resolveType(unresolvedInterfaces[i]);
        }
    }

    public BasicType getUnresolvedSuperclassType() {
        return unresolvedSuper;
    }

    public Type getResolvedSuperclassType() {
        return resolvedSuper;
    }

    public BasicType[] getUnresolvedInterfaceTypes() {
        return unresolvedInterfaces;
    }

    public Type[] getResolvedInterfaceTypes() {
        return resolvedInterfaces;
    }
}
