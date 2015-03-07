package me.august.lumen.compile.ast;

import me.august.lumen.common.ModifierSet;
import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.ClassCodeGen;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import org.objectweb.asm.ClassVisitor;

public class FieldNode extends Typed implements ClassCodeGen {

    private String name;
    private ModifierSet modifiers;
    private Expression defaultValue;

    public FieldNode(String name, UnresolvedType type, ModifierSet modifiers) {
        super(type);
        this.name = name;
        this.modifiers = modifiers;
    }

    public Expression getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Expression defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public ModifierSet getModifiers() {
        return modifiers;
    }

    @Override
    public void generate(ClassVisitor visitor, BuildContext context) {
        // `null, null` represents `signature (generics), value`
        visitor.visitField(
            modifiers.getValue(), getName(), getResolvedType().getDescriptor(),
            null, null // signature, value
        );
    }

    @Override
    public String toString() {
        return "FieldNode{" +
            "name='" + name + '\'' +
            ", type='" + unresolvedType + '\'' +
            ", modifiers=" + modifiers +
            ", defaultValue=" + defaultValue +
            '}';
    }
}
