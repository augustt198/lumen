package me.august.lumen.common;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BytecodeUtil implements Opcodes {

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

    public static Type fromNamedType(String string) {
        switch (string) {
            case "int":     return Type.INT_TYPE;
            case "bool":
            case "boolean": return Type.BOOLEAN_TYPE;
            case "byte":    return Type.BYTE_TYPE;
            case "float":   return Type.FLOAT_TYPE;
            case "double":  return Type.DOUBLE_TYPE;
            case "long":    return Type.LONG_TYPE;
            case "char":    return Type.CHAR_TYPE;
            case "short":   return Type.SHORT_TYPE;
            case "void":    return Type.VOID_TYPE;
            default:        return Type.getType("L" + string.replace('.', '/') + ";");
        }
    }

    public static void pushInt(MethodVisitor method, int num) {
        // we can use a iconst_<i>
        if (num >= -1 && num <= 5) {
            int opcode;
            switch (num) {
                case -1: opcode = ICONST_M1; break;
                case  0: opcode = ICONST_0;  break;
                case  1: opcode = ICONST_1;  break;
                case  2: opcode = ICONST_2;  break;
                case  3: opcode = ICONST_3;  break;
                case  4: opcode = ICONST_4;  break;
                case  5: opcode = ICONST_5;  break;
                default: opcode = -1;
            }
            method.visitInsn(opcode);
        // in byte range, use bipush
        } else if (num >= Byte.MIN_VALUE && num <= Byte.MAX_VALUE) {
            method.visitIntInsn(BIPUSH, num);
        // in short range, use sipush
        } else if (num >= Short.MIN_VALUE && num <= Short.MAX_VALUE) {
            method.visitIntInsn(SIPUSH, num);
        // load constant
        } else {
            method.visitLdcInsn(num);
        }
    }

    public static void pushDouble(MethodVisitor method, double num) {
        if (num == 0) {
            method.visitInsn(DCONST_0);
        } else if (num == 1) {
            method.visitInsn(DCONST_1);
        } else {
            method.visitLdcInsn(num);
        }
    }

    public static Type numberType(Class<? extends Number> cls) {
        switch (cls.getSimpleName()) {
            case "Float":   return Type.FLOAT_TYPE;
            case "Double":  return Type.DOUBLE_TYPE;
            case "Integer": return Type.INT_TYPE;
            case "Long":    return Type.LONG_TYPE;
            case "Short":   return Type.SHORT_TYPE;
            case "Byte":    return Type.BYTE_TYPE;
            default:        return null;
        }
    }

    public static int castNumberOpcode(Type orig, Type target) {
        switch (orig.getSort()) {
            case Type.INT:
                switch (target.getSort()) {
                    case Type.BYTE:   return I2B;
                    case Type.CHAR:   return I2C;
                    case Type.DOUBLE: return I2D;
                    case Type.FLOAT:  return I2F;
                    case Type.LONG:   return I2L;
                    case Type.SHORT:  return I2S;
                    default:          return -1;
                }
            case Type.LONG:
                switch (target.getSort()) {
                    case Type.DOUBLE: return L2D;
                    case Type.FLOAT:  return L2F;
                    case Type.INT:    return L2I;
                    default:          return -1;
                }
            case Type.FLOAT:
                switch (target.getSort()) {
                    case Type.DOUBLE: return F2D;
                    case Type.INT:    return F2I;
                    case Type.LONG:   return F2L;
                    default:          return -1;
                }
            case Type.DOUBLE:
                switch (target.getSort()) {
                    case Type.FLOAT: return D2F;
                    case Type.INT:   return D2I;
                    case Type.LONG:  return D2L;
                    default:         return -1;
                }
        }
        return -1;
    }

    // byte, char, short, int, long, float, double
    private static final String PRIMITIVE_ORDER = "BCSIJFD";

    public static boolean canWidenTo(Type orig, Type target) {
        // orig is not in the char-double range
        if (orig.getSort() < Type.CHAR || orig.getSort() > Type.DOUBLE)
            return false;
        // target is not in the char-double range
        if (target.getSort() < Type.CHAR || target.getSort() > Type.DOUBLE)
            return false;

        return PRIMITIVE_ORDER.indexOf(orig.getDescriptor()) <= PRIMITIVE_ORDER.indexOf(target.getDescriptor());
    }
}
