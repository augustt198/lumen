package me.august.lumen.common;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

public final class BytecodeUtil {

    private BytecodeUtil() {}

    public static String[] splitTypes(String desc) {
        List<String> types = new ArrayList<>();

        for (int i = 0; i < desc.length(); i++) {
            switch (desc.charAt(i)) {
                case 'Z':
                case 'B':
                case 'C':
                case 'S':
                case 'I':
                case 'J':
                case 'F':
                case 'D':
                case 'V':
                    types.add(desc.charAt(i) + "");
                    break;
                case '[':
                case 'L':
                    StringBuilder sb = new StringBuilder();
                    while (desc.charAt(i) != ';') {
                        sb.append(desc.charAt(i));
                        i++;
                    }
                    types.add(sb.toString());
                    break;
            }
        }

        return types.toArray(new String[types.size()]);
    }

    public static String toType(Class<?> cls) {
        switch (cls.getName()) {
            case "boolean":
                return "Z";
            case "byte":
            case "char":
            case "short":
            case "int":
            case "float":
            case "double":
            case "void":
                return cls.getName().substring(0, 1).toUpperCase();
            case "long":
                return "J";
            default:
                if (cls.isArray()) {
                    return '[' + toType(cls.getComponentType());
                } else {
                    return 'L' + cls.getName().replace('.', '/') + ';';
                }
        }
    }

    public static int storeInstruction(Type type) {
        return type.getOpcode(Opcodes.ISTORE);
    }

    public static int loadInstruction(Type type) {
        return type.getOpcode(Opcodes.ILOAD);
    }
}
