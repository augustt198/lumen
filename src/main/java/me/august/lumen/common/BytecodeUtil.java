package me.august.lumen.common;

import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.*;

public final class BytecodeUtil implements Opcodes {

    public static final String[] LUMEN_PRIMITIVES = {
        "int", "byte", "boolean", "bool", "long",
        "char", "double", "float", "void"
    };

    public static final Set<String> LUMEN_PRIMITIVES_SET =
        new HashSet<>(Arrays.asList(LUMEN_PRIMITIVES));

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

    /**
     * Gets the xSTORE instruction for the given type.
     * @param type The type to be stored
     * @return The xSTORE instruction for the given type
     */
    public static int storeInstruction(Type type) {
        return type.getOpcode(ISTORE);
    }

    /**
     * Gets the xLOAD instruction for the given type.
     * @param type The type to be loaded
     * @return The xSTORE instruction for the given type
     */
    public static int loadInstruction(Type type) {
        return type.getOpcode(ILOAD);
    }

    /**
     * Gets the xADD instruction for the given type.
     * @param type The type to be added
     * @return The xADD instruction for the given type
     */
    public static int addInstruction(Type type) {
        return type.getOpcode(IADD);
    }

    /**
     * Gets the xSUB instruction for the given type.
     * @param type The type to be subtracted
     * @return The xSUB instruction for the given type
     */
    public static int subtractInstruction(Type type) {
        return type.getOpcode(ISUB);
    }

    /**
     * Gets the xMUL instruction for the given type.
     * @param type The type to be multiplied
     * @return The xMUL instruction for the given type
     */
    public static int multiplyInstruction(Type type) {
        return type.getOpcode(IMUL);
    }

    /**
     * Gets the xDIV instruction for the given type.
     * @param type The type to be divided
     * @return The xDIV instruction for the given type
     */
    public static int divisionInstruction(Type type) {
        return type.getOpcode(IDIV);
    }

    /**
     * Gets the xREM instruction for the given type.
     * @param type The type of the remainder operands
     * @return The xREM instruction for the given type
     */
    public static int remainderInstruction(Type type) {
        return type.getOpcode(IREM);
    }

    /**
     * Gets the xNEG instruction for the given type.
     * @param type The type to be negated
     * @return The xNEG instruction for the given type
     */
    public static int negateInstruction(Type type) {
        return type.getOpcode(INEG);
    }

    public static int fromOpcodeName(String name) {
        return OPCODES.getOrDefault(name, -1);
    }

    public static String toOpcodeName(int opcode) {
        for (Map.Entry<String, Integer> entry : OPCODES.entrySet()) {
            if (entry.getValue() == opcode)
                return entry.getKey();
        }

        return null;
    }

    public static int fromAccName(String name) {
        return ACC_MODS.getOrDefault(name, -1);
    }

    public static Type fromSimpleName(String string) {
        return Type.getType(getDescriptor(string));
    }

    public static Type fromSimpleName(String string, int dims) {
        return Type.getType(getDescriptor(string, dims));
    }

    public static String getDescriptor(String str) {
        switch (str) {
            case "int":     return "I";
            case "bool":
            case "boolean": return "Z";
            case "byte":    return "B";
            case "float":   return "F";
            case "double":  return "D";
            case "long":    return "L";
            case "char":    return "C";
            case "short":   return "S";
            case "void":    return "V";
            default:        return "L" + str.replace('.', '/') + ";";
        }
    }

    public static String getDescriptor(String str, int dims) {
        String base = getDescriptor(str);
        return StringUtil.repeat('[', dims) + base;
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


    private static Set<Type> BOXED_TYPES;

    static {
        Class[] classes = new Class[]{
            Byte.class, Character.class, Short.class, Integer.class,
            Long.class, Float.class, Double.class
        };

        BOXED_TYPES = new HashSet<>(classes.length);

        for (Class cls : classes)
            BOXED_TYPES.add(Type.getType(cls));
    }

    public static boolean isBoxedType(Type type) {
        return BOXED_TYPES.contains(type);
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

    public static int compareWidth(Type t1, Type t2) {
        if (!isNumeric(t1) || !isNumeric(t2)) return Integer.MAX_VALUE;

        int idx1 = PRIMITIVE_ORDER.indexOf(t1.getDescriptor());
        int idx2 = PRIMITIVE_ORDER.indexOf(t2.getDescriptor());

        if (idx1 > idx2) {
            return 1;
        } else if (idx1 < idx2) {
            return -1;
        } else {
            return 0;
        }
    }

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

    public static boolean isObject(Type type) {
        return type.getSort() == Type.OBJECT;
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

    public static void createArray(MethodVisitor method, BuildContext ctx, Type type, Expression length) {
        length.generate(method, ctx);

        int typeOpcode = toArrayTypeOpcode(type);
        if (typeOpcode >= 0) {
            method.visitIntInsn(NEWARRAY, typeOpcode);
        } else {
            method.visitTypeInsn(ANEWARRAY, type.getInternalName());
        }
    }

    public static int toArrayTypeOpcode(Type type) {
        switch (type.getSort()) {
            case Type.BOOLEAN: return T_BOOLEAN;
            case Type.BYTE:    return T_BYTE;
            case Type.CHAR:    return T_CHAR;
            case Type.SHORT:   return T_SHORT;
            case Type.INT:     return T_INT;
            case Type.LONG:    return T_LONG;
            case Type.FLOAT:   return T_FLOAT;
            case Type.DOUBLE:  return T_DOUBLE;
            default:           return -1;
        }
    }

    public static int negateCondition(int opcode) {
        switch (opcode) {
            case IFNULL:    return IFNONNULL;
            case IFNONNULL: return IFNULL;

            case IFEQ:      return IFNE;
            case IFNE:      return IFEQ;
            case IFLT:      return IFGE;
            case IFLE:      return IFGT;
            case IFGT:      return IFLE;
            case IFGE:      return IFLT;

            case IF_ICMPEQ: return IF_ICMPNE;
            case IF_ICMPNE: return IF_ICMPEQ;
            case IF_ICMPLT: return IF_ICMPGE;
            case IF_ICMPLE: return IF_ICMPGT;
            case IF_ICMPGT: return IF_ICMPLE;
            case IF_ICMPGE: return IF_ICMPLT;

            case IF_ACMPEQ: return IF_ACMPNE;
            case IF_ACMPNE: return IF_ACMPEQ;

            default:        return -1;
        }
    }

    public static int compareOpcode(Type type) {
        switch (type.getSort()) {
            case Type.DOUBLE: return DCMPL;
            case Type.FLOAT:  return FCMPL;
            case Type.LONG:   return LCMP;
            default:          return -1;
        }
    }

    // Type#getOpcode doesn't work on xRETURN
    // opcodes for some reason...
    public static int returnOpcode(Type type) {
        switch (type.getSort()) {
            case Type.INT:    return IRETURN;
            case Type.LONG:   return LRETURN;
            case Type.FLOAT:  return FRETURN;
            case Type.DOUBLE: return DRETURN;
            case Type.OBJECT: return ARETURN;
            case Type.VOID:   return RETURN;
            default:          return IRETURN;
        }
    }

    public static int arrayLoadOpcode(Type type) {
        switch (type.getSort()) {
            case Type.BYTE:   return BALOAD;
            case Type.CHAR:   return CALOAD;
            case Type.SHORT:  return SALOAD;
            case Type.INT:    return IALOAD;
            case Type.LONG:   return LALOAD;
            case Type.FLOAT:  return FALOAD;
            case Type.DOUBLE: return DALOAD;
            case Type.ARRAY:
            case Type.OBJECT: return AALOAD;
            default:          return -1;
        }
    }

    public static int arrayStoreOpcode(Type type) {
        switch (type.getSort()) {
            case Type.BYTE:   return BASTORE;
            case Type.CHAR:   return CASTORE;
            case Type.SHORT:  return SASTORE;
            case Type.INT:    return IASTORE;
            case Type.LONG:   return LALOAD;
            case Type.FLOAT:  return FASTORE;
            case Type.DOUBLE: return DASTORE;
            case Type.ARRAY:
            case Type.OBJECT: return AASTORE;
            default:          return -1;
        }
    }

    public static Type componentType(Type type) {
        // not an array
        if (type.getDimensions() < 1) return null;

        String descriptor = type.getDescriptor();
        return Type.getType(descriptor.substring(1, descriptor.length()));
    }

    public static Type toBoxedType(Type type) {
        switch (type.getSort()) {
            case Type.BYTE:   return Type.getType(Byte.class);
            case Type.CHAR:   return Type.getType(Character.class);
            case Type.SHORT:  return Type.getType(Short.class);
            case Type.INT:    return Type.getType(Integer.class);
            case Type.LONG:   return Type.getType(Long.class);
            case Type.FLOAT:  return Type.getType(Float.class);
            case Type.DOUBLE: return Type.getType(Double.class);
            default:          return null;
        }
    }

    public static Type toUnboxedType(Type type) {
        String name = type.getClassName();
        switch (name) {
            case "java.lang.Byte":      return Type.BYTE_TYPE;
            case "java.lang.Character": return Type.CHAR_TYPE;
            case "java.lang.Short":     return Type.SHORT_TYPE;
            case "java.lang.Integer":   return Type.INT_TYPE;
            case "java.lang.Long":      return Type.LONG_TYPE;
            case "java.lang.Float":     return Type.FLOAT_TYPE;
            case "java.lang.Double":    return Type.DOUBLE_TYPE;
            default:                    return null;
        }
    }

}
