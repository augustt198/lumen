package me.august.lumen.compile.analyze.var;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

public interface VariableReference {

    Type getType();
    void generateGetCode(MethodVisitor m);
    void generateSetCode(MethodVisitor m, Consumer<MethodVisitor> insn);

}
