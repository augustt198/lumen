package me.august.lumen.compile.ast;

import me.august.lumen.common.ModifierSet;
import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.analyze.types.ClassTypeInfo;
import me.august.lumen.compile.analyze.types.TypeInfo;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.ClassCodeGen;
import me.august.lumen.compile.resolve.type.BasicType;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassNode implements ClassCodeGen, VisitorConsumer, TypeInfoProducer {

    private ModifierSet modifiers;
    private String name;
    private String[] interfaces;

    private List<FieldNode> fields = new ArrayList<>();
    private List<MethodNode> methods = new ArrayList<>();

    private ClassTypeInfo typeInfo;

    public ClassNode(String name, BasicType superClass, String[] interfaces, ModifierSet modifiers) {
        this.name       = name;

        BasicType[] itfs = Arrays.stream(interfaces)
                .map(BasicType::new)
                .toArray(s -> new BasicType[interfaces.length]);

        typeInfo = new ClassTypeInfo(superClass, itfs);
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
            typeInfo.getResolvedSuperclassType().getInternalName(),
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
            typeInfo.getResolvedSuperclassType().getInternalName(),
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
                "fields=" + fields +
                ", modifiers=" + modifiers +
                ", name='" + name + '\'' +
                ", interfaces=" + Arrays.toString(interfaces) +
                ", methods=" + methods +
                ", typeInfo=" + typeInfo +
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

    public String[] getInterfaces() {
        return interfaces;
    }

    @Override
    public ClassTypeInfo getTypeInfo() {
        return typeInfo;
    }

}
