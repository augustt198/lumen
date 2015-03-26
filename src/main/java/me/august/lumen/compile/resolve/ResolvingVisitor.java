package me.august.lumen.compile.resolve;

import me.august.lumen.compile.resolve.impl.NameResolver;
import me.august.lumen.compile.resolve.type.BasicType;
import org.objectweb.asm.Type;

public class ResolvingVisitor extends TypeVisitor {

    public ResolvingVisitor(NameResolver resolver) {
        super(resolver);
    }

}
