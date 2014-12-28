package me.august.lumen.compile.resolve;

import org.objectweb.asm.Type;

public interface TypeResolver {

    Type resolveType(String simpleName);

}
