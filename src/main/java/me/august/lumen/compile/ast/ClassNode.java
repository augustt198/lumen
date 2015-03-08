package me.august.lumen.compile.ast;

import me.august.lumen.common.ModifierSet;
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

    private ModifierSet modifiers;
    private String name;
    private TypedNode superClass;
    private String[] interfaces;

    private List<FieldNode> fields = new ArrayList<>();
    private List<MethodNode> methods = new ArrayList<>();

    public ClassNode(String name, TypedNode superClass, String[] interfaces, ModifierSet modifiers) {
        this.name       = name;
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.modifiers  = modifiers;
    }

    @Override
    public void generate(ClassVisitor visitor, BuildContext context) {
        visitor.visit(
            context.classVersion(),
            modifiers.getValue(),
            name,
            null,
            superClass.getResolvedType().getInternalName(),
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
            Opcodes.INVOKESPECIAL,
            superClass.getResolvedType().getInternalName(),
            "<init>", "()V", false
        );
        method.visitInsn(Opcodes.RETURN);
        method.visitMaxs(1, 1);

        method.visitEnd();
    }

    @Override
    public void acceptTopDown(ASTVisitor visitor) {
        visitor.visitClass(this);

        fields.forEach(visitor::visitField);
        methods.forEach((m) -> m.acceptTopDown(visitor));

        visitor.visitClassEnd(this);
    }

    @Override
    public void acceptBottomUp(ASTVisitor visitor) {
        visitor.visitClass(this);

        fields.forEach(visitor::visitField);
        methods.forEach((m) -> m.acceptBottomUp(visitor));

        visitor.visitClassEnd(this);
    }

    @Override
    public String toString() {
        return "ClassNode{" +
            "modifiers=" + modifiers +
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

    public ModifierSet getModifiers() {
        return modifiers;
    }

    public String getName() {
        return name;
    }

    public TypedNode getSuperClass() {
        return superClass;
    }

    public String[] getInterfaces() {
        return interfaces;
    }

    public void setSuperClass(TypedNode superClass) {
        this.superClass = superClass;
    }
}
