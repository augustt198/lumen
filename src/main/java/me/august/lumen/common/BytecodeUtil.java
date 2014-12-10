package me.august.lumen.common;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BytecodeUtil {

    private static final Map<String, Integer> OPCODES  = new HashMap<>();
    private static final Map<String, Integer> ACC_MODS = new HashMap<>();

    static {
        initOpcodeTable();
        initAccessTable();
    }

    private static final int ASM_OPCODE_FIELDS_START_INDEX = 60;
    private static final int ASM_OPCODE_FIELDS_END_INDEX   = 216;

    private static void initOpcodeTable() {
        int start = ASM_OPCODE_FIELDS_START_INDEX;
        int end   = ASM_OPCODE_FIELDS_END_INDEX;

        for (int i = start; i <= end; i++) {
            Field field = Opcodes.class.getFields()[i];
            try {
                OPCODES.put(field.getName(), field.getInt(null));
            } catch (IllegalAccessException ignored) {}
        }
    }

    private static final int ASM_ACC_FIELDS_START_INDEX = 10;
    private static final int ASM_ACC_FIELDS_END_INDEX   = 29;

    private static void initAccessTable() {
        int start = ASM_ACC_FIELDS_START_INDEX;
        int end   = ASM_ACC_FIELDS_END_INDEX;

        for (int i = start; i <= end; i++) {
            Field field = Opcodes.class.getFields()[i];
            try {
                ACC_MODS.put(field.getName(), field.getInt(null));
            } catch (IllegalAccessException ignored) {}
        }
    }

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

    public static int fromOpcodeName(String name) {
        return OPCODES.getOrDefault(name, -1);
    }

    public static int fromAccName(String name) {
        return ACC_MODS.getOrDefault(name, -1);
    }
}
