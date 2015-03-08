package me.august.lumen.compile.resolve.lookup;

import me.august.lumen.compile.resolve.data.ClassData;
import me.august.lumen.compile.resolve.lookup.factory.ReflectionClassDataFactory;

public class BuiltinClassLookup implements ClassLookup {

    @Override
    public ClassData lookup(String path) {
        Class<?> cls = safeForName(path);
        if (cls == null) return null;

        return ReflectionClassDataFactory.createClassData(cls);
    }

    @Override
    public boolean hasClass(String path) {
        return safeForName(path) != null;
    }

    private Class<?> safeForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
