package me.august.lumen.compile.resolve.impl;

import me.august.lumen.compile.parser.ast.ImportNode;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.resolve.QualifiedNameResolver;
import me.august.lumen.compile.resolve.data.ClassData;
import me.august.lumen.compile.resolve.lookup.DependencyManager;

public class NameResolver implements QualifiedNameResolver {

    private DependencyManager dependencies;
    private ImportNode[] imports;

    public NameResolver(DependencyManager dependencies, ImportNode... imports) {
        this.dependencies = dependencies;
        this.imports = imports;
    }

    @Override
    public ClassData fromSimpleName(String simpleName, ProgramNode prgm) {
        String fullName = null;
        for (ImportNode impt : imports) {
            if (impt.getPath().endsWith(simpleName)) {
                fullName = impt.getPath();
            }
        }
        // Not found in any important statements
        if (fullName == null) return null;

        ClassData data = dependencies.lookup(fullName);

        if (data == null) {
            try {
                // check to see if class is imported by "default" (java.lang.*)
                data = ClassData.fromClass(Class.forName("java.lang." + simpleName));
            } catch (ClassNotFoundException ignored) {}
        }

        return data;
    }
}
