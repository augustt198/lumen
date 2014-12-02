package me.august.lumen.analyze.var;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

// Massively simplified - will only get/set fields
// of `this` (current Object)
public class ClassVariable implements Variable, Opcodes {

    String cls;
    private String name;
    private String type;

    public ClassVariable(String cls, String name, String type) {
        this.cls = cls;
        this.name = name;
        this.type = type;
    }

    @Override
    public void generateGetCode(MethodVisitor m) {
        // TODO get fields of other objects
        m.visitVarInsn(ALOAD, 0);
        m.visitFieldInsn(GETFIELD, cls, name, type);
    }

    @Override
    public void generateSetCode(MethodVisitor m, Consumer<MethodVisitor> insn) {
        // TODO set fields of other objects
        m.visitVarInsn(ALOAD, 0);
        insn.accept(m);
        m.visitFieldInsn(PUTFIELD, cls, name, type);
    }
}
