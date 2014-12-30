package me.august.lumen.compile.analyze.var;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

public class StaticVariable implements VariableReference {

    private String className;
    private String fieldName;
    private Type   fieldType;

    public StaticVariable(String className, String fieldName, Type fieldType) {
        this.className = className;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    @Override
    public void generateGetCode(MethodVisitor m) {
        m.visitFieldInsn(
            Opcodes.GETSTATIC,
            className,
            fieldName,
            fieldType.getDescriptor()
        );
    }

    @Override
    public void generateSetCode(MethodVisitor m, Consumer<MethodVisitor> insn) {
        if (insn != null) insn.accept(m);
        m.visitFieldInsn(
            Opcodes.PUTSTATIC,
            className,
            fieldName,
            fieldType.getDescriptor()
        );
    }

    @Override
    public Type getType() {
        return fieldType;
    }
}
