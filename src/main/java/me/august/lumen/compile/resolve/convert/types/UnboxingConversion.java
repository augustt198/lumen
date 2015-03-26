package me.august.lumen.compile.resolve.convert.types;

import me.august.lumen.common.BytecodeUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class UnboxingConversion extends Conversion {

    public UnboxingConversion(Type original, Type target) {
        super(original, target);
    }

    public UnboxingConversion(Type original) {
        super(original, BytecodeUtil.toUnboxedType(original));
    }

    @Override
    public void applyConversion(MethodVisitor visitor) {
        String methodName = getUnboxingMethod(getOriginal());
        visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                getOriginal().getInternalName(),
                methodName,
                // e.g. ()I
                "()" + getTarget().getDescriptor(),
                false
        );
    }

    private static String getUnboxingMethod(Type type) {
        switch (type.getClassName()) {
            case "java.lang.Integer"    : return "intValue";
            case "java.lang.Long"       : return "longValue";
            case "java.lang.Short"      : return "shortValue";
            case "java.lang.Byte"       : return "byteValue";
            case "java.lang.Float"      : return "floatValue";
            case "java.lang.Double"     : return "doubleValue";
            case "java.lang.Boolean"    : return "booleanValue";
            case "java.lang.Character"  : return "charValue";

            default: return null;
        }
    }
}
