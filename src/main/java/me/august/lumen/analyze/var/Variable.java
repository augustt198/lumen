package me.august.lumen.analyze.var;

import org.objectweb.asm.MethodVisitor;

import java.util.function.Consumer;

public interface Variable {

    void generateGetCode(MethodVisitor m);
    void generateSetCode(MethodVisitor m, Consumer<MethodVisitor> insn);

}
