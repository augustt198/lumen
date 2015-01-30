package me.august.lumen.compile.resolve.data;

import me.august.lumen.common.Modifier;
import me.august.lumen.compile.resolve.lookup.ClassLookup;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Skeletal representation of a Java class.
 *
 * Stores class version, methods, fields,
 * super class, and implemented interfaces.
 */
public class ClassData extends BaseData {

    int version;

    List<MethodData> methods = new ArrayList<>();
    List<FieldData> fields   = new ArrayList<>();

    String superClass;
    String[] interfaces;

    public ClassData(String name, Modifier... modifiers) {
        super(name, modifiers);
    }

    public static ClassData fromClassFile(InputStream input) throws IOException {
        ClassReader reader = new ClassReader(input);
        ClassAnalyzer analyzer = new ClassAnalyzer();
        reader.accept(analyzer, 0);

        return analyzer.classData;
    }

    public static ClassData fromClass(Class<?> cls) {
        ClassData data = new ClassData(cls.getName(), Modifier.fromAccess(cls.getModifiers()));

        if (cls.getSuperclass() != null)
            data.superClass = cls.getSuperclass().getName();

        String[] interfaces = new String[cls.getInterfaces().length];
        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i] = cls.getInterfaces()[i].getName();
        }
        data.interfaces = interfaces;

        for (Method method : cls.getDeclaredMethods()) {
            Type returnType = Type.getType(method.getReturnType());
            Type[] paramsTypes = new Type[method.getParameterCount()];
            for (int i = 0; i < paramsTypes.length; i++) {
                paramsTypes[i] = Type.getType(method.getParameterTypes()[i]);
            }
            Modifier[] mods = Modifier.fromAccess(method.getModifiers());
            MethodData methodData = new MethodData(method.getName(), returnType, paramsTypes, mods);

            data.getMethods().add(methodData);
        }

        for (Field field : cls.getDeclaredFields()) {
            Modifier[] mods = Modifier.fromAccess(field.getModifiers());
            Type type = Type.getType(field.getType());
            FieldData fieldData = new FieldData(field.getName(), type, mods);

            data.getFields().add(fieldData);
        }
        return data;
    }

    public boolean isAssignableTo(String type, ClassLookup lookup) {
        return isAssignableTo(type, lookup, 0) > -1;
    }

    public int assignableDistance(Type type, ClassLookup lookup) {
        if (type.getSort() == Type.OBJECT)
            return assignableDistance(type.getClassName(), lookup);
        else
            return -1;
    }

    public int assignableDistance(String type, ClassLookup lookup) {
        return isAssignableTo(type, lookup, 0);
    }

    private int isAssignableTo(String type, ClassLookup lookup, int dist) {
        if (type.equals(getName()))
            return dist;

        String sup;
        if (isInterface() && superClass == null) {
            sup = Object.class.getName();
        } else {
            sup = superClass;
        }

        if (sup != null) {
            ClassData supClass = lookup.lookup(sup);
            if (supClass != null) {
                int supDist = supClass.isAssignableTo(type, lookup, dist + 1);
                if (supDist > -1)
                    return supDist;
            }
        }


        for (String itf : interfaces) {
            ClassData itfClass = lookup.lookup(itf);
            if (itf != null) {
                int itfDist = itfClass.isAssignableTo(type, lookup, dist + 1);
                if (itfClass.isAssignableTo(type, lookup))
                    return itfDist;
            }
        }

        return -1;
    }

    @Override
    public String toString() {
        return "ClassData{" +
            "version=" + version +
            ", methods=" + methods +
            ", fields=" + fields +
            ", superClass='" + superClass + '\'' +
            ", interfaces=" + Arrays.toString(interfaces) +
            '}';
    }

    public List<MethodData> getMethods() {
        return methods;
    }

    public List<FieldData> getFields() {
        return fields;
    }

    public String getSuperClass() {
        return superClass;
    }

    public String[] getInterfaces() {
        return interfaces;
    }

    public FieldData getField(String name) {
        for (FieldData field : fields) {
            if (field.getName().equals(name)) return field;
        }
        return null;
    }

    public List<MethodData> getMethods(String name) {
        List<MethodData> found = new ArrayList<>();
        for (MethodData method : methods) {
            if (method.getName().equals(name)) found.add(method);
        }

        return found;
    }

    public boolean isInterface() {
        for (Modifier mod : modifiers)
            if (mod == Modifier.INTERFACE) return true;

        return false;
    }

    private static class ClassAnalyzer extends ClassVisitor {
        ClassData classData;

        public ClassAnalyzer() {
            super(Opcodes.ASM5);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String sup, String[] impls) {
            Modifier[] modifiers = Modifier.fromAccess(access);
            classData = new ClassData(name, modifiers);
            classData.version    = version;
            classData.superClass = sup;
            classData.interfaces = impls;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exs) {
            Type methodType = Type.getMethodType(desc);
            Type returnType = methodType.getReturnType();
            Type[] types    = methodType.getArgumentTypes();

            MethodData method = new MethodData(name, returnType, types, Modifier.fromAccess(access));
            classData.methods.add(method);

            return super.visitMethod(access, name, desc, sig, exs);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String sig, Object val) {
            Type type = Type.getType(desc);
            FieldData field = new FieldData(name, type, Modifier.fromAccess(access));
            classData.fields.add(field);
            return super.visitField(access, name, desc, sig, val);
        }
    }

}
