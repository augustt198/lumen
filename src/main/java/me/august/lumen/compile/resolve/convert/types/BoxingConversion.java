package me.august.lumen.compile.resolve.convert.types;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class BoxingConversion extends Conversion {

    public BoxingConversion(Type original, Type target) {
        super(original, target);
    }

    @Override
    public void applyConversion(MethodVisitor visitor) {
        // Call boxed type's valueOf method
        visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                getTarget().getInternalName(),
                "valueOf",
                // e.g. (I)Ljava/langInteger;
                "(" + getOriginal().getDescriptor() + ")" + getTarget().getDescriptor(),
                false
        );
    }
}
