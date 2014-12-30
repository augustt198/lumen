package me.august.lumen.compile.resolve.impl;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.parser.ast.ImportNode;
import me.august.lumen.compile.resolve.TypeResolver;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NameResolver implements TypeResolver {

    private static final Set<String> keywordTypes
        = new HashSet<>(Arrays.asList(BytecodeUtil.LUMEN_PRIMITIVES));

    private ImportNode[] imports;

    public NameResolver(ImportNode... imports) {
        this.imports = imports;
    }

    @Override
    public Type resolveType(String simpleName) {
        if (keywordTypes.contains(simpleName))
            return BytecodeUtil.fromNamedType(simpleName);

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

        return fullName == null ? null : Type.getObjectType(fullName);
    }

}
