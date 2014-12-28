package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.Typed;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class StaticField extends Typed implements Expression {

    private String className;
    private String fieldName;

    private Type type;

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
    public void generate(MethodVisitor visitor, BuildContext context) {
        visitor.visitFieldInsn(
            Opcodes.GETSTATIC,
            getResolvedType().getDescriptor(),
            fieldName,
            type.getDescriptor()
        );
    }
}
