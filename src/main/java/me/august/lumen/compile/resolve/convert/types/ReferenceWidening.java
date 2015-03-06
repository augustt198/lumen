package me.august.lumen.compile.resolve.convert.types;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class ReferenceWidening extends Conversion {
    public ReferenceWidening(Type original, Type target) {
        super(original, target);
    }

    @Override
    public void applyConversion(MethodVisitor visitor) {
        // do nothing
    }
}
