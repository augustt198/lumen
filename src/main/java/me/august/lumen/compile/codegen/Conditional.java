package me.august.lumen.compile.codegen;

import org.objectweb.asm.Opcodes;

public interface Conditional {

    public static final MethodCodeGen PUSH_TRUE  =
        (m, c) -> m.visitInsn(Opcodes.ICONST_1);

    public static final MethodCodeGen PUSH_FALSE =
        (m, c) -> m.visitInsn(Opcodes.ICONST_0);

    Branch branch(MethodCodeGen ifBranch, MethodCodeGen elseBranch);

}
