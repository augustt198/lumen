package me.august.lumen.compile.resolve.convert;

import me.august.lumen.compile.resolve.convert.types.BoxingConversion;
import me.august.lumen.compile.resolve.convert.types.PrimitiveWidening;
import me.august.lumen.compile.resolve.convert.types.ReferenceWidening;
import me.august.lumen.compile.resolve.convert.types.UnboxingConversion;
import me.august.lumen.compile.resolve.data.ClassData;
import me.august.lumen.compile.resolve.data.MethodData;
import me.august.lumen.compile.resolve.data.exception.AmbiguousMethodException;
import me.august.lumen.compile.resolve.lookup.ClassLookup;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static me.august.lumen.common.BytecodeUtil.*;

public class Conversions {

    private Conversions() {}

    public static ConversionStrategy convert(Type original, Type target, ClassLookup lookup) {
        ConversionStrategy cs = new ConversionStrategy();
        convert(original, target, cs, lookup);

        return cs;
    }

    private static void convert(Type original, Type target, ConversionStrategy cs, ClassLookup lookup) {
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
                convert(boxed, target, cs, lookup);
            }
        } else if (isObject(original) && isPrimitive(target)) {
            Type unboxed = toUnboxedType(original);
            cs.addStep(new UnboxingConversion(original, unboxed));

            // unboxed primitive type is not target primitive type
            if (!target.equals(unboxed)) {
                convert(unboxed, target, cs, lookup);
            }
        } else if (isObject(original) && isObject(target)) {
            ClassData data = lookup.lookup(original.getClassName());
            if (data.isAssignableTo(target.getClassName(), lookup)) {
                cs.addStep(new ReferenceWidening(original, target));
            } else {
                cs.addStep(null);
            }
        }
    }

    public static MethodData pickMethod(Type[] types, MethodData[] methods, ClassLookup lookup)
        throws AmbiguousMethodException {

        TreeMap<Integer, List<MethodData>> subTypeRanks = new TreeMap<>();

        outer:
        for (MethodData method : methods) {
            if (method.getParamTypes().length != types.length)
                continue;

            int rank = 0;
            for (int i = 0; i < method.getParamTypes().length; i++) {
                Type paramType = method.getParamTypes()[i];
                if (paramType.getSort() != Type.OBJECT)
                    continue outer;

                ClassData actualTypeClass = lookup.lookup(types[i]);
                if (actualTypeClass == null)
                    continue outer;

                int dist = actualTypeClass.assignableDistance(paramType, lookup);
                if (dist > -1)
                    rank += dist;
                else
                    continue outer;
            }

            List<MethodData> list = subTypeRanks.get(rank);
            if (list == null) {
                list = new ArrayList<>();
                list.add(method);
                subTypeRanks.put(rank, list);
            } else {
                list.add(method);
            }
        }

        if (!subTypeRanks.isEmpty()) {
            List<MethodData> applicable = subTypeRanks.firstEntry().getValue();
            if (applicable.size() > 1)
                throw new AmbiguousMethodException(applicable);
            else
                return applicable.get(0);
        }

        // TODO consider other types of type conversions
        return null;
    }

}
