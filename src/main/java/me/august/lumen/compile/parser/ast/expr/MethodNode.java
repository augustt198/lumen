package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.common.Modifier;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.ClassCodeGen;
import me.august.lumen.compile.parser.ast.Typed;
import me.august.lumen.compile.parser.ast.code.Body;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.*;

public class MethodNode extends Typed implements VisitorConsumer, ClassCodeGen {

    private String name;
    private Modifier[] modifiers;

    private Map<String, String> parameters = new HashMap<>();

    private Body body;

    public MethodNode(String name, String returnType, Modifier... modifiers) {
        super(returnType);
        this.name = name;
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
        Type returnType   = BytecodeUtil.fromNamedType(getResolvedType());
        Type[] paramTypes = parameters.values().stream().map(BytecodeUtil::fromNamedType).toArray(Type[]::new);

        MethodVisitor method = visitor.visitMethod(
            Modifier.compose(modifiers), name, // access modifier
            Type.getMethodType(returnType, paramTypes).getDescriptor(), // method descriptor
            null, null // signature (generics), exceptions
        );

        method.visitCode();
        body.generate(method, context);
        method.visitInsn(Opcodes.RETURN);

        method.visitEnd();
    }

    @Override
    public void setResolvedType(String resolvedType) {
        super.setResolvedType(resolvedType == null ? "void" : resolvedType);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Modifier[] getModifiers() {
        return modifiers;
    }

    public void setModifiers(Modifier[] modifiers) {
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
        return "MethodNode{" +
            "parameters=" + parameters +
            ", name='" + name + '\'' +
            ", modifiers=" + Arrays.toString(modifiers) +
            ", type='" + type + '\'' +
            ", body=" + body +
            '}';
    }
}
