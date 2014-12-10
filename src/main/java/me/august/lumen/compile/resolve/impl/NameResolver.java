package me.august.lumen.compile.resolve.impl;

import me.august.lumen.compile.parser.ast.ImportNode;
import me.august.lumen.compile.resolve.QualifiedNameResolver;

public class NameResolver implements QualifiedNameResolver {

    private ImportNode[] imports;

    public NameResolver(ImportNode... imports) {
        this.imports = imports;
    }

    @Override
    public String getQualifiedName(String simpleName) {
        String fullName = null;
        for (ImportNode impt : imports) {
            if (impt.getPath().endsWith(simpleName)) {
                fullName = impt.getPath();
            }
        }

        if (fullName == null) {
            try {
                fullName = Class.forName("java.lang." + simpleName).getName();
            } catch (ClassNotFoundException ignored) {}
        }
        return fullName;
    }
}
