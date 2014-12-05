package me.august.lumen.compile.analyze.var;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.MethodVisitor;

import java.util.function.Consumer;

public class LocalVariable implements Variable, Opcodes {

    int index;

    public LocalVariable(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void generateGetCode(MethodVisitor m) {
        m.visitVarInsn(ALOAD, index);
    }

    @Override
    public void generateSetCode(MethodVisitor m, Consumer<MethodVisitor> insn) {
        insn.accept(m);
        m.visitVarInsn(ASTORE, index);
    }
}
