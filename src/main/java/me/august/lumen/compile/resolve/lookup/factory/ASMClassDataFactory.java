package me.august.lumen.compile.resolve.lookup.factory;

import me.august.lumen.common.ModifierSet;
import me.august.lumen.compile.resolve.data.ClassData;
import me.august.lumen.compile.resolve.data.FieldData;
import me.august.lumen.compile.resolve.data.MethodData;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;

public final class ASMClassDataFactory {

    private ASMClassDataFactory() {}

    public static ClassData createClassData(InputStream input) throws IOException {
        return createClassData(new ClassReader(input));
    }

    public static ClassData createClassData(ClassReader reader) {
        ClassCreator creator = new ClassCreator();
        reader.accept(creator, 0);

        return creator.classData;
    }

    private static class ClassCreator extends ClassVisitor {
        ClassData classData;

        public ClassCreator() {
            super(Opcodes.ASM5);
        }

        // version = class's file version
        // access  = class's access modifiers
        // name    = class's name
        // sig     = class's signature
        // sup     = class's superclass
        // itfs    = class's implemented interfaces
        @Override
        public void visit(int version, int access, String name, String sig,
                          String sup, String[] itfs) {

            ModifierSet modifiers = new ModifierSet(access);

            this.classData = new ClassData(name, modifiers, version, sup, itfs);
        }

        // access = method's access modifiers
        // name   = method's name
        // desc   = method's descriptor
        // sig    = method's signature
        // exs    = exceptions thrown by method
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                                         String sig, String[] exs) {

            Type methodType = Type.getMethodType(desc);
            Type returnType = methodType.getReturnType();
            Type[] argTypes = methodType.getArgumentTypes();

            MethodData method = new MethodData(
                    name, returnType, argTypes, new ModifierSet(access)
            );

            // TODO make this immutable somehow
            this.classData.getMethods().add(method);

            return super.visitMethod(access, name, desc, sig, exs);
        }

        // access = field's access modifiers
        // name   = field's name
        // desc   = field's descriptor
        // sig    = field's signature
        // val    = field's default value (only works for certain
        //          types of values)
        @Override
        public FieldVisitor visitField(int access, String name, String desc, String sig, Object val) {
            Type type = Type.getType(desc);

            FieldData field = new FieldData(name, type, new ModifierSet(access));
            this.classData.getFields().add(field);

            return super.visitField(access, name, desc, sig, val);
        }
    }


}
