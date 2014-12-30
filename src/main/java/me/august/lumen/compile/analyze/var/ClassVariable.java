package me.august.lumen.compile.analyze.var;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

public class ClassVariable implements VariableReference, Opcodes {

    String cls;
    private String name;
    private Type type;

    public ClassVariable(String cls, String name, Type type) {
        this.cls  = cls;
        this.name = name;
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void generateGetCode(MethodVisitor m) {
        m.visitVarInsn(ALOAD, 0);
        m.visitFieldInsn(GETFIELD, cls, name, type.getDescriptor());
    }

    @Override
    public void generateSetCode(MethodVisitor m, Consumer<MethodVisitor> insn) {
        m.visitVarInsn(ALOAD, 0);
        insn.accept(m);
        m.visitFieldInsn(PUTFIELD, cls, name, type.getDescriptor());
    }
}
