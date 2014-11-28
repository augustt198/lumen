package me.august.lumen.compile.parser.ast;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.MethodCodeGen;
import org.objectweb.asm.MethodVisitor;

public interface CodeBlock extends MethodCodeGen {

    default void generate(MethodVisitor visitor, BuildContext context) { }

}
