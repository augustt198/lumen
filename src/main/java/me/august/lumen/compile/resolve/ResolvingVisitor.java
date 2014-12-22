package me.august.lumen.compile.resolve;

import me.august.lumen.compile.resolve.impl.NameResolver;

public class ResolvingVisitor extends TypeVisitor {

    private NameResolver resolver;

    public ResolvingVisitor(NameResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public String resolveType(String simple) {
        return resolver.getQualifiedName(simple);
    }
}
