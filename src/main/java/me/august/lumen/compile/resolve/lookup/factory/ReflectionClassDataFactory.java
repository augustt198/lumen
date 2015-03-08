package me.august.lumen.compile.resolve.lookup.factory;

import me.august.lumen.common.ModifierSet;
import me.august.lumen.compile.resolve.data.ClassData;
import me.august.lumen.compile.resolve.data.FieldData;
import me.august.lumen.compile.resolve.data.MethodData;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class ReflectionClassDataFactory {

    private static Map<Class<?>, ClassData> CACHE = new HashMap<>();

    private ReflectionClassDataFactory() {}

    public static ClassData createClassData(Class<?> cls) {
        if (CACHE.containsKey(cls)) {
            return CACHE.get(cls);
        }

        String superClass = null;
        if (cls.getSuperclass() != null) {
            superClass = cls.getSuperclass().getName();
        }

        Class<?>[] impls = cls.getInterfaces();
        String[] itfs = new String[impls.length];
        for (int i = 0; i < impls.length; i++) {
            itfs[i] = impls[i].getName();
        }

        ClassData classData = new ClassData(
                cls.getName(), new ModifierSet(cls.getModifiers()),
                0,superClass, itfs
        );

        for (Method method : cls.getMethods()) {
            classData.getMethods().add(createMethod(method));
        }

        for (Field field : cls.getFields()) {
            classData.getFields().add(createField(field));
        }

        CACHE.put(cls, classData);

        return classData;
    }

    private static MethodData createMethod(Method method) {
        Type returnType = Type.getType(method.getReturnType());

        Class<?>[] clsParamTypes = method.getParameterTypes();
        Type[] paramTypes = new Type[clsParamTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            paramTypes[i] = Type.getType(clsParamTypes[i]);
        }

        ModifierSet mods = new ModifierSet(method.getModifiers());

        return new MethodData(method.getName(), returnType, paramTypes, mods);
    }

    private static FieldData createField(Field field) {
        Type type = Type.getType(field.getType());
        ModifierSet mods = new ModifierSet(field.getModifiers());

        return new FieldData(field.getName(), type, mods);
    }

}
