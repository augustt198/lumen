package me.august.lumen.compile.codegen;

import jdk.internal.org.objectweb.asm.ClassVisitor;

public interface ClassCodeGen {

    void generate(ClassVisitor visitor, BuildContext context);

}
