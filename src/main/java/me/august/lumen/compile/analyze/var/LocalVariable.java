package me.august.lumen.compile.analyze.var;

import me.august.lumen.common.BytecodeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

public class LocalVariable implements Variable, Opcodes {

    private int index;
    private Type type;

    public LocalVariable(int index, Type type) {
        this.index = index;
        this.type  = type;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void generateGetCode(MethodVisitor m) {
        m.visitVarInsn(BytecodeUtil.loadInstruction(type), index);
    }

    @Override
    public void generateSetCode(MethodVisitor m, Consumer<MethodVisitor> insn) {
        insn.accept(m);
        m.visitVarInsn(BytecodeUtil.storeInstruction(type), index);
    }
}
