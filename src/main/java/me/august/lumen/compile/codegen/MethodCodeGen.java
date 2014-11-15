package me.august.lumen.compile.codegen;

import org.objectweb.asm.MethodVisitor;

public interface MethodCodeGen {

    void generate(MethodVisitor visitor, BuildContext context);

}
