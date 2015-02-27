package me.august.lumen.common;

import me.august.lumen.compile.resolve.data.ClassData;
import me.august.lumen.compile.resolve.lookup.ClassLookup;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public final class TypeUtil {

    private TypeUtil() {}

    public static Set<ClassData> commonAncestors(ClassData a, ClassData b, ClassLookup lookup) {
        TreeMap<Integer, Set<ClassData>> map = new TreeMap<>();
        commonAncestors(a, b, 0, map, lookup);

        if (map.isEmpty()) {
            return Collections.emptySet();
        } else {
            return map.firstEntry().getValue();
        }
    }

    private static void commonAncestors(ClassData a, ClassData b, int dist, TreeMap<Integer, Set<ClassData>> map,
                                        ClassLookup lookup) {
        if (a == b) {
            if (map.containsKey(dist)) {
                map.get(dist).add(a);
            } else {
                Set<ClassData> set = new HashSet<>();
                set.add(a);
                map.put(dist, set);
            }
        }

        if (a.getSuperclass() != null) {
            ClassData sup = lookup.lookup(a.getSuperclass());
            if (sup != null) {
                commonAncestors(sup, b, dist + 1, map, lookup);
            }
        }

        for (String cls : a.getInterfaces()) {
            ClassData itf = lookup.lookup(cls);
            if (itf != null) {
                commonAncestors(itf, b, dist + 1, map, lookup);
            }
        }

        if (b.getSuperclass() != null) {
            ClassData sup = lookup.lookup(b.getSuperclass());
            if (sup != null) {
                commonAncestors(a, sup, dist + 1, map, lookup);
            }
        }

        for (String cls : b.getInterfaces()) {
            ClassData itf = lookup.lookup(cls);
            if (itf != null) {
                commonAncestors(a, itf, dist + 1, map, lookup);
            }
        }
    }


}
