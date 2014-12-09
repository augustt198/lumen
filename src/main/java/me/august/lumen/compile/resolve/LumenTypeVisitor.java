package me.august.lumen.compile.resolve;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.Typed;
import me.august.lumen.compile.resolve.impl.NameResolver;

public class LumenTypeVisitor extends TypeVisitor {

    private NameResolver resolver;
    private BuildContext build;

    public LumenTypeVisitor(NameResolver resolver, BuildContext build) {
        this.resolver = resolver;
        this.build = build;
    }

    @Override
    public void visitType(Typed node) {
        String resolved = resolver.getQualifiedName(node.getType());
        node.setResolvedType(resolved);
    }
}
