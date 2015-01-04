package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.analyze.var.StaticVariable;
import me.august.lumen.compile.analyze.var.VariableReference;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.Typed;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class StaticField extends Typed implements VariableExpression {

    private String className;
    private String fieldName;

    private Type type;
    private VariableReference variableReference;

    public StaticField(String className, String fieldName) {
        super(className);
        this.className = className;
        this.fieldName = fieldName;
    }

    public String getClassName() {
        return className;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public Type expressionType() {
        return type;
    }

    @Override
    public VariableReference getVariableReference() {
        if (variableReference != null) return variableReference;

        return variableReference = new StaticVariable(
            getResolvedType().getInternalName(), fieldName, type
        );
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        getVariableReference().generateGetCode(visitor);
        /*
        visitor.visitFieldInsn(
            Opcodes.GETSTATIC,
            getResolvedType().getClassName(),
            fieldName,
            type.getDescriptor()
        );
        */
    }
}
