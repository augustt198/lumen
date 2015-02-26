package me.august.lumen.compile.resolve.convert.types;

import me.august.lumen.common.BytecodeUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class PrimitiveWidening extends Conversion {

    public PrimitiveWidening(Type original, Type target) {
        super(original, target);
    }

    @Override
    public void applyConversion(MethodVisitor visitor) {
        int op = BytecodeUtil.castNumberOpcode(getOriginal(), getTarget());
        visitor.visitInsn(op);
    }
}
