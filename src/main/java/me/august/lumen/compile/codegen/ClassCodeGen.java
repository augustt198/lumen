package me.august.lumen.compile.codegen;

import org.objectweb.asm.ClassVisitor;

/**
 * Interface for generating bytecode at the class level
 */
public interface ClassCodeGen {

    void generate(ClassVisitor visitor, BuildContext context);

}
