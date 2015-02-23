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

    /**
     * Creates a ConversionStrategy from converting from the original
     * type to the target type.
     * @param original The original type
     * @param target   The target Type
     * @param lookup   Source for class lookups
     * @return A ConversionStrategy
     */
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

    /**
     * Picks the suitable overloaded method based on the
     * given types. Throws an AmbiguousMethodException if
     * two or more methods' types have equal distance to
     * the given types.
     * @param types The actual argument type
     * @param methods The methods to pick from
     * @param lookup The ClassLookup to use
     * @return The applicable method
     * @throws AmbiguousMethodException
     */
    public static MethodData pickMethod(Type[] types, MethodData[] methods, ClassLookup lookup)
        throws AmbiguousMethodException {

        // distance rank -> [methods]
        // (lower rank is closer)
        // examples:
        // distance of Object to Object = 0
        // distance of String to Object = 1
        // distance of RuntimeException to Throwable = 2
        TreeMap<Integer, List<MethodData>> subTypeRanks = new TreeMap<>();

        // outer loop label for continuing to next method
        outer:
        for (MethodData method : methods) {
            // method cannot be applicable if argument lengths differ
            if (method.getParamTypes().length != types.length)
                continue;

            // distance rank
            int rank = 0;
            for (int i = 0; i < method.getParamTypes().length; i++) {
                Type paramType = method.getParamTypes()[i];
                if (paramType.getSort() != Type.OBJECT) {
                    // don't consider method for distance ranking
                    // if argument isn't an object
                    // TODO include primitive distance for ranking?
                    continue outer;
                }

                ClassData actualTypeClass = lookup.lookup(types[i]);
                if (actualTypeClass == null) {
                    // can't resolve argument type, method not applicable
                    // (we should probably throw an exception...)
                    continue outer;
                }

                // distance from actual argument type to method's argument type
                int dist = actualTypeClass.assignableDistance(paramType, lookup);
                // nonnegative distance implies types are compatible
                // via a reference widening conversion
                if (dist > -1) {
                    rank += dist;
                } else {
                    // actual argument type and method's argument type
                    // are not compatible, go to next method
                    continue outer;
                }
            }

            // associate ranking with the method

            // value type is a list because multiple methods can have
            // the same ranking
            List<MethodData> list = subTypeRanks.get(rank);
            if (list == null) {
                list = new ArrayList<>();
                list.add(method);
                subTypeRanks.put(rank, list);
            } else {
                list.add(method);
            }
        }

        // at least one method was ranked
        if (!subTypeRanks.isEmpty()) {
            // list of methods with lowest ranking
            List<MethodData> applicable = subTypeRanks.firstEntry().getValue();


            if (applicable.size() > 1) {
                // multiple methods had the same lowest ranking
                throw new AmbiguousMethodException(applicable);
            } else {
                // list has one element, get first
                return applicable.get(0);
            }
        }

        // TODO consider other types of type conversions
        return null;
    }

}
