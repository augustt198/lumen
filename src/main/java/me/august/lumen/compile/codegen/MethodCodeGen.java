package me.august.lumen.compile.codegen;

import jdk.internal.org.objectweb.asm.MethodVisitor;

public interface MethodCodeGen {

    void generate(MethodVisitor visitor, BuildContext context);

}
