package me.august.lumen.compile.resolve.data;

import me.august.lumen.common.ModifierSet;
import me.august.lumen.compile.resolve.lookup.ClassLookup;
import org.objectweb.asm.Type;

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

    public ClassData(String name, ModifierSet modifiers) {
        super(name, modifiers);
    }

    public ClassData(String name, ModifierSet mods, int version, String sup, String[] itfs) {
        super(name, mods);
        this.version = version;
        this.superClass = sup;
        this.interfaces = itfs;
    }

    public boolean isAssignableTo(String type, ClassLookup lookup) {
        return assignableDistance(type, lookup, 0) > -1;
    }

    public boolean isAssignableTo(Type type, ClassLookup lookup) {
        return isAssignableTo(type.getClassName(), lookup);
    }

    public boolean isAssignableTo(ClassData cls, ClassLookup lookup) {
        return isAssignableTo(cls.getName(), lookup);
    }

    public int assignableDistance(Type type, ClassLookup lookup) {
        if (type.getSort() == Type.OBJECT) {
            return assignableDistance(type.getClassName(), lookup);
        } else {
            return -1;
        }
    }

    public int assignableDistance(String type, ClassLookup lookup) {
        return assignableDistance(type, lookup, 0);
    }

    private int assignableDistance(String type, ClassLookup lookup, int dist) {
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
                int supDist = supClass.assignableDistance(type, lookup, dist + 1);
                if (supDist > -1)
                    return supDist;
            }
        }


        for (String itf : interfaces) {
            ClassData itfClass = lookup.lookup(itf);
            if (itf != null) {
                int itfDist = itfClass.assignableDistance(type, lookup, dist + 1);
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

    public String getSuperclass() {
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
        return modifiers.isInterface();
    }

}
