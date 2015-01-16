package me.august.lumen.compile.resolve.convert.types;

import org.objectweb.asm.Type;

public class UnboxingConversion extends Conversion {
    public UnboxingConversion(Type original, Type target) {
        super(original, target);
    }
}
