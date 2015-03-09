package me.august.lumen.compile.analyze.types;

import me.august.lumen.compile.resolve.TypeResolver;
import me.august.lumen.compile.resolve.type.BasicType;
import org.objectweb.asm.Type;

public class MethodTypeInfo implements TypeInfo {

    private BasicType unresolvedReturn;
    private Type resolvedReturn;

    private BasicType[] unresolvedParams;
    private Type[] resolvedParams;

    public MethodTypeInfo(BasicType unresolvedReturn, BasicType... unresolvedParams) {
        this.unresolvedReturn = unresolvedReturn;
        this.unresolvedParams = unresolvedParams;
    }

    @Override
    public void resolve(TypeResolver resolver) {
        resolvedReturn = resolver.resolveType(unresolvedReturn);

        if (resolvedParams == null) {
            resolvedParams = new Type[unresolvedParams.length];
        }
        for (int i = 0; i < unresolvedParams.length; i++) {
            resolvedParams[i] = resolver.resolveType(unresolvedParams[i]);
        }
    }

    public BasicType getUnresolvedReturnType() {
        return unresolvedReturn;
    }

    public Type getResolvedReturnType() {
        return resolvedReturn;
    }

    public BasicType[] getUnresolvedParameterTypes() {
        return unresolvedParams;
    }

    public Type[] getResolvedParameterTypes() {
        return resolvedParams;
    }
}
