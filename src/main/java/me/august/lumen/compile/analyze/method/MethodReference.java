package me.august.lumen.compile.analyze.method;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.MethodCodeGen;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public abstract class MethodReference implements MethodCodeGen {

    private String owner;
    private String name;
    private Type descriptor;
    private boolean itf;
    private boolean shouldPop;

    public MethodReference(String owner, String name, Type descriptor, boolean itf, boolean shouldPop) {
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
        this.itf = itf;
        this.shouldPop = shouldPop;
    }

    protected abstract int getOpcode();

    public MethodReference(String owner, String name, Type descriptor) {
        this(owner, name, descriptor, false, false);
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        visitor.visitMethodInsn(getOpcode(), owner, name, descriptor.getDescriptor(), itf);
        if (shouldPop) {
            visitor.visitInsn(Opcodes.POP);
        }
    }

    public static class Instance extends MethodReference {
        public Instance(String owner, String name, Type descriptor) {
            super(owner, name, descriptor);
        }

        public Instance(String owner, String name, Type descriptor, boolean itf, boolean shouldPop) {
            super(owner, name, descriptor, itf, shouldPop);
        }

        @Override
        protected int getOpcode() {
            return Opcodes.INVOKEVIRTUAL;
        }
    }

    public static class Static extends MethodReference {
        public Static(String owner, String name, Type descriptor, boolean itf, boolean shouldPop) {
            super(owner, name, descriptor, itf, shouldPop);
        }

        public Static(String owner, String name, Type descriptor) {
            super(owner, name, descriptor);
        }

        @Override
        protected int getOpcode() {
            return Opcodes.INVOKESTATIC;
        }
    }
}
