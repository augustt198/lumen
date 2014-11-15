package me.august.lumen.compile.parser.ast;

import org.objectweb.asm.ClassVisitor;
import me.august.lumen.common.Modifier;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.ClassCodeGen;

import java.util.Arrays;

public class ClassNode implements ClassCodeGen {

    private Modifier[] modifiers;
    private String name;
    private String superClass;
    private String[] interfaces;

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
    }

    @Override
    public String toString() {
        return "ClassNode{" +
            "modifiers=" + Arrays.toString(modifiers) +
            ", name='" + name + '\'' +
            ", superClass='" + superClass + '\'' +
            ", interfaces=" + Arrays.toString(interfaces) +
            '}';
    }
}
