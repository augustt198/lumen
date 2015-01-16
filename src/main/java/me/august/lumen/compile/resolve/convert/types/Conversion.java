package me.august.lumen.compile.resolve.convert.types;

import org.objectweb.asm.Type;

public class Conversion {

    private Type original;
    private Type target;

    public Conversion(Type original, Type target) {
        this.original = original;
        this.target = target;
    }

    public Type getOriginal() {
        return original;
    }

    public Type getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '{' +
            "original=" + original +
            ", target=" + target +
            '}';
    }
}
