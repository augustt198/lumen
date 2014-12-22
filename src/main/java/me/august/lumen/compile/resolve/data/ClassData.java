package me.august.lumen.compile.resolve.data;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.common.Modifier;
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
        data.superClass = cls.getSuperclass().getName();
        String[] interfaces = new String[cls.getInterfaces().length];
        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i] = cls.getInterfaces()[i].toString();
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
            String[] parts = desc.split("\\)"); // ["(params...", returnType]
            Type returnType = Type.getType(parts[1]);

            // remove first char '('
            String[] params = BytecodeUtil.splitTypes(parts[0].substring(1, parts[0].length()));
            Type[] types = new Type[params.length];
            for (int i = 0; i < types.length; i++) {
                types[i] = Type.getType(params[i]);
            }

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
