package me.august.lumen.compile.resolve.impl;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.ast.ImportNode;
import me.august.lumen.compile.resolve.TypeResolver;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import org.objectweb.asm.Type;

public class NameResolver implements TypeResolver {

    private ImportNode[] imports;

    public NameResolver(ImportNode... imports) {
        this.imports = imports;
    }

    @Override
    public Type resolveType(UnresolvedType unresolved) {
        // if unresolved type is a primitive or primitive
        // array, no resolution is needed
        if (unresolved.baseIsPrimitive())
            return unresolved.toType();

        String baseName = unresolved.getBaseName();
        String fullName = null;
        for (ImportNode impt : imports) {
            if (impt.hasClass(baseName)) {
                fullName = impt.getFull(baseName);
            }
        }

        if (fullName == null) {
            try {
                fullName = Class.forName("java.lang." + baseName).getName();
            } catch (ClassNotFoundException ignored) {}
        }

        if (fullName == null) {
            return null;
        } else {
            return BytecodeUtil.fromSimpleName(fullName, unresolved.getArrayDimensions());
        }
    }

}
