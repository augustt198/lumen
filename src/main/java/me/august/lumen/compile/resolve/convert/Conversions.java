package me.august.lumen.compile.resolve.convert;

import me.august.lumen.compile.resolve.convert.types.BoxingConversion;
import me.august.lumen.compile.resolve.convert.types.PrimitiveWidening;
import me.august.lumen.compile.resolve.convert.types.ReferenceWidening;
import me.august.lumen.compile.resolve.convert.types.UnboxingConversion;
import org.objectweb.asm.Type;

import static me.august.lumen.common.BytecodeUtil.*;

public class Conversions {

    private Conversions() {}

    public static ConversionStrategy convert(Type original, Type target) {
        ConversionStrategy cs = new ConversionStrategy();
        convert(original, target, cs);

        return cs;
    }

    private static void convert(Type original, Type target, ConversionStrategy cs) {
        if (isNumeric(original) && isNumeric(target)) {
            // target is wider than original
            if (compareWidth(original, target) < 0) {
                cs.addStep(new PrimitiveWidening(original, target));
            } else {
                cs.addStep(null);
            }
        } else if (isPrimitive(original) && isObject(target)) {
            Type boxed = toBoxedType(original);
            cs.addStep(new BoxingConversion(original, boxed));

            // boxed object type is not target object type
            if (!target.equals(boxed)) {
                convert(boxed, target, cs);
            }
        } else if (isObject(original) && isPrimitive(target)) {
            Type unboxed = toUnboxedType(original);
            cs.addStep(new UnboxingConversion(original, unboxed));

            // unboxed primitive type is not target primitive type
            if (!target.equals(unboxed)) {
                convert(unboxed, target, cs);
            }
        } else if (isObject(original) && isObject(target)) {
            cs.addStep(new ReferenceWidening(original, target));
        }
    }

}
