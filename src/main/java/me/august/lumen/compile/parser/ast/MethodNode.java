package me.august.lumen.compile.parser.ast;

import me.august.lumen.common.ModifierSet;
import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.ClassCodeGen;
import me.august.lumen.compile.parser.ast.stmt.Body;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;

public class MethodNode extends Typed implements VisitorConsumer, ClassCodeGen {

    private String name;
    private ModifierSet modifiers;

    private List<Parameter> parameters;

    private Body body;

    public MethodNode(String name, UnresolvedType returnType, List<Parameter> parameters, ModifierSet modifiers) {
        super(returnType);
        this.name = name;
        this.parameters = parameters;
        this.modifiers = modifiers;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitMethod(this);
        body.accept(visitor);
        visitor.visitMethodEnd(this);
    }

    @Override
    public void generate(ClassVisitor visitor, BuildContext context) {
        Type returnType   = getResolvedType();
        Type[] paramTypes = parameters.stream().map(Parameter::getResolvedType).toArray(Type[]::new);

            MethodVisitor method = visitor.visitMethod(
                modifiers.getValue(), name, // access modifier
                Type.getMethodType(returnType, paramTypes).getDescriptor(), // method descriptor
                null, null // signature (generics), exceptions
            );

        method.visitCode();
        body.generate(method, context);

        if (getResolvedType().getSort() == Type.VOID)
            method.visitInsn(Opcodes.RETURN);

        // TODO *actually* compute maxs
        method.visitMaxs(10, 10);
        method.visitEnd();
    }

    public boolean isStatic() {
        return modifiers.isStatic();
    }

    @Override
    public void setResolvedType(Type resolvedType) {
        super.setResolvedType(resolvedType == null ? Type.VOID_TYPE : resolvedType);
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModifierSet getModifiers() {
        return modifiers;
    }

    public void setModifiers(ModifierSet modifiers) {
        this.modifiers = modifiers;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public boolean hasBody() {
        return body != null;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
