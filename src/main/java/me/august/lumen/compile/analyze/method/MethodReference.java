package me.august.lumen.compile.analyze.method;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.MethodCodeGen;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MethodReference implements MethodCodeGen {

    private String owner;
    private String name;
    private Type descriptor;
    private boolean itf;

    public MethodReference(String owner, String name, Type descriptor, boolean itf) {
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
        this.itf = itf;
    }

    public MethodReference(String owner, String name, Type descriptor) {
        this(owner, name, descriptor, false);
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, name, descriptor.getDescriptor(), itf);
    }
}
