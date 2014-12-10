package me.august.lumen.compile.parser.ast.code;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.analyze.var.LocalVariable;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.parser.ast.Typed;
import me.august.lumen.compile.parser.ast.expr.Expression;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class VarDeclaration extends Typed implements CodeBlock {

    private String name;
    private Expression defaultValue;

    private LocalVariable ref;

    public VarDeclaration(String name, String type) {
        this(name, type, null);
    }

    public VarDeclaration(String name, String type, Expression defaultValue) {
        super(type);
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public Expression getDefaultValue() {
        return defaultValue;
    }

    public LocalVariable getRef() {
        return ref;
    }

    public void setRef(LocalVariable ref) {
        this.ref = ref;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        // nothing to generate if there is no expression, or if
        // we don't know *where* to store this variable
        if (defaultValue == null || ref == null) return;

        Type type = BytecodeUtil.fromNamedType(getResolvedType());

        // get expression value
        defaultValue.generate(visitor, context);

        int index = ref.getIndex();
        // store expression at index
        visitor.visitVarInsn(BytecodeUtil.storeInstruction(type), index);
        int opcode = BytecodeUtil.storeInstruction(type);
    }
}
