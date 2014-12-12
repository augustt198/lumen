package me.august.lumen.compile.resolve.impl;

import me.august.lumen.compile.parser.ast.ImportNode;
import me.august.lumen.compile.resolve.QualifiedNameResolver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NameResolver implements QualifiedNameResolver {

    private static final Set<String> keywordTypes;

    static {
        String[] types = {
            "int", "byte", "boolean", "long",
            "char", "double", "float", "void"
        };
        keywordTypes = new HashSet<>(Arrays.asList(types));
    }

    private ImportNode[] imports;

    public NameResolver(ImportNode... imports) {
        this.imports = imports;
    }

    @Override
    public String getQualifiedName(String simpleName) {
        if (keywordTypes.contains(simpleName)) return simpleName;

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
