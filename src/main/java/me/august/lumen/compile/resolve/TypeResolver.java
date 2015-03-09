package me.august.lumen.compile.resolve;

import me.august.lumen.compile.resolve.type.BasicType;
import org.objectweb.asm.Type;

public interface TypeResolver {

    Type resolveType(BasicType unresolvedType);

}
