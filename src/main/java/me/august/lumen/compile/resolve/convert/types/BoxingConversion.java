package me.august.lumen.compile.resolve.convert.types;

import org.objectweb.asm.Type;

public class BoxingConversion extends Conversion {
    public BoxingConversion(Type original, Type target) {
        super(original, target);
    }
}
