package me.august.lumen.compile.parser.ast;

import me.august.lumen.common.Modifier;
import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.ClassCodeGen;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassNode implements ClassCodeGen, VisitorConsumer {

    private Modifier[] modifiers;
    private String name;
    private String superClass;
    private String[] interfaces;

    private List<FieldNode> fields = new ArrayList<>();
    private List<MethodNode> methods = new ArrayList<>();

    public ClassNode(String name, String superClass, String[] interfaces, Modifier... modifiers) {
        this.name       = name;
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.modifiers  = modifiers;
    }

    @Override
    public void generate(ClassVisitor visitor, BuildContext context) {
        visitor.visit(
            context.classVersion(),
            Modifier.compose(modifiers),
            name,
            null,
            superClass,
            interfaces
        );

        generateDefaultConstructor(visitor);

        for (FieldNode field : fields) field.generate(visitor, context);

        for (MethodNode method : methods) method.generate(visitor, context);
    }

    private void generateDefaultConstructor(ClassVisitor visitor) {
        MethodVisitor method = visitor.visitMethod(
            Opcodes.ACC_PUBLIC, "<init>", "()V", null, null
        );

        // load `this`
        method.visitVarInsn(Opcodes.ALOAD, 0);
        method.visitMethodInsn(
            Opcodes.INVOKESPECIAL, superClass, "<init>", "()V", false
        );
        method.visitInsn(Opcodes.RETURN);
        method.visitMaxs(1, 1);

        method.visitEnd();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitClass(this);

        fields.forEach(visitor::visitField);
        for (MethodNode method : methods) {
            method.accept(visitor);
        }

        visitor.visitClassEnd(this);
    }

    @Override
    public String toString() {
        return "ClassNode{" +
            "modifiers=" + Arrays.toString(modifiers) +
            ", name='" + name + '\'' +
            ", superClass='" + superClass + '\'' +
            ", interfaces=" + Arrays.toString(interfaces) +
            ", fields=" + fields +
            ", methods=" + methods +
            '}';
    }

    public List<FieldNode> getFields() {
        return fields;
    }

    public List<MethodNode> getMethods() {
        return methods;
    }

    public Modifier[] getModifiers() {
        return modifiers;
    }

    public String getName() {
        return name;
    }

    public String getSuperClass() {
        return superClass;
    }

    public String[] getInterfaces() {
        return interfaces;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }
}
