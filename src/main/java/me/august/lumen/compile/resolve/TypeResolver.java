package me.august.lumen.compile.resolve;

import me.august.lumen.compile.resolve.type.UnresolvedType;
import org.objectweb.asm.Type;

public interface TypeResolver {

    Type resolveType(UnresolvedType unresolvedType);

}
