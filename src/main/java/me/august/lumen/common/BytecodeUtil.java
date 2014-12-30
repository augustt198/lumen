package me.august.lumen.common;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.expr.Expression;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BytecodeUtil implements Opcodes {

    public static final String[] LUMEN_PRIMITIVES = {
        "int", "byte", "boolean", "bool", "long",
        "char", "double", "float", "void"
    };

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
        return type.getOpcode(ISTORE);
    }

    public static int loadInstruction(Type type) {
        return type.getOpcode(ILOAD);
    }

    public static int addInstruction(Type type) {
        return type.getOpcode(IADD);
    }

    public static int subtractInstruction(Type type) {
        return type.getOpcode(ISUB);
    }

    public static int multiplyInstruction(Type type) {
        return type.getOpcode(IMUL);
    }

    public static int divisionInstruction(Type type) {
        return type.getOpcode(IDIV);
    }

    public static int remainderInstruction(Type type) {
        return type.getOpcode(IREM);
    }

    public static int negateInstruction(Type type) {
        return type.getOpcode(INEG);
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

    public static void pushLong(MethodVisitor method, long num) {
        if (num == 0) {
            method.visitInsn(LCONST_0);
        } else if (num == 1) {
            method.visitInsn(LCONST_1);
        } else {
            method.visitLdcInsn(num);
        }
    }

    public static void pushFloat(MethodVisitor method, float num) {
        if (num == 0) {
            method.visitInsn(FCONST_0);
        } else if (num == 1) {
            method.visitInsn(FCONST_1);
        } else if (num == 2) {
            method.visitInsn(FCONST_2);
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

    public static void pushNull(MethodVisitor method) {
        method.visitInsn(ACONST_NULL);
    }

    public static boolean isPrimitive(Type type) {
        return type.getSort() >= Type.BOOLEAN && type.getSort() <= Type.DOUBLE;
    }

    public static boolean isIntegral(Type type) {
        return (type.getSort() >= Type.BYTE && type.getSort() <= Type.INT)
            || type.getSort() == Type.LONG;
    }

    public static boolean isNumeric(Type type) {
        return type.getSort() >= Type.CHAR && type.getSort() <= Type.DOUBLE;
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
        // both types must be numeric
        if (!isNumeric(orig) || !isNumeric(target)) return false;

        return PRIMITIVE_ORDER.indexOf(orig.getDescriptor()) <= PRIMITIVE_ORDER.indexOf(target.getDescriptor());
    }

    public static Type widest(Type t1, Type t2) {
        // both types must be numeric
        if (!isNumeric(t1) || !isNumeric(t2)) return null;

        if (PRIMITIVE_ORDER.indexOf(t1.getDescriptor()) <= PRIMITIVE_ORDER.indexOf(t2.getDescriptor())) {
            return t2;
        } else {
            return t1;
        }
    }

    public static final Type STRING_TYPE = Type.getType(String.class);

    public static boolean isString(Type type) {
        return type.equals(STRING_TYPE);
    }

    private static final Type SB_TYPE = Type.getType(StringBuilder.class);

    public static void concatStringsBytecode(MethodVisitor method, BuildContext ctx, Expression... exprs) {
        // initialize StringBuilder instance
        method.visitTypeInsn(NEW, SB_TYPE.getInternalName());
        method.visitInsn(DUP);
        method.visitMethodInsn(
            INVOKESPECIAL, SB_TYPE.getInternalName(),
            "<init>", Type.getMethodType(Type.VOID_TYPE).getDescriptor(),
            false // not an interface
        );

        // append expressions
        for (Expression expr : exprs) {
            Type type = Type.getMethodType(SB_TYPE, expr.expressionType());
            expr.generate(method, ctx);

            method.visitMethodInsn(
                INVOKEVIRTUAL, SB_TYPE.getInternalName(),
                "append", type.getDescriptor(),
                false // not an interface
            );
        }

        // finally, call toString()
        method.visitMethodInsn(
            INVOKEVIRTUAL, SB_TYPE.getInternalName(),
            "toString", Type.getMethodType(STRING_TYPE).getDescriptor(),
            false
        );
    }
}
