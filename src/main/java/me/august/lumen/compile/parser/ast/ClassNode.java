package me.august.lumen.compile.parser.ast;

import me.august.lumen.common.Modifier;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.ClassCodeGen;
import me.august.lumen.compile.parser.ast.expr.MethodNode;
import org.objectweb.asm.ClassVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassNode implements ClassCodeGen {

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

        for (FieldNode field : fields) {
            visitor.visitField(
                Modifier.compose(field.getModifiers()),
                field.getName(),
                field.getType(),
                null,
                null
            );
        }
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
}
