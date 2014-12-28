package me.august.lumen.compile.resolve;

import me.august.lumen.compile.resolve.impl.NameResolver;
import org.objectweb.asm.Type;

public class ResolvingVisitor extends TypeVisitor {

    private NameResolver resolver;

    public ResolvingVisitor(NameResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Type resolveType(String simple) {
        return resolver.resolveType(simple);
    }
}
