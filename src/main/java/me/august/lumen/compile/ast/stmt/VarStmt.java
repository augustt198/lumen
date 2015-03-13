package me.august.lumen.compile.ast.stmt;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.analyze.var.LocalVariable;
import me.august.lumen.compile.ast.CodeBlock;
import me.august.lumen.compile.ast.SingleTypedNode;
import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.resolve.type.BasicType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class VarStmt extends SingleTypedNode implements CodeBlock, VisitorConsumer {

    private String name;
    private Expression defaultValue;

    private LocalVariable ref;

    public VarStmt(String name, BasicType type) {
        this(name, type, null);
    }

    public VarStmt(String name, BasicType type, Expression defaultValue) {
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
    public void acceptTopDown(ASTVisitor visitor) {
        visitor.visitVar(this);

        if (defaultValue != null) {
            defaultValue.acceptTopDown(visitor);
        }
    }

    @Override
    public void acceptBottomUp(ASTVisitor visitor) {
        defaultValue.acceptBottomUp(visitor);
        visitor.visitVar(this);
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        // nothing to generate if there is no expression, or if
        // we don't know *where* to store this variable
        if (defaultValue == null || ref == null) return;

        Type type = getResolvedType();

        // get expression value
        defaultValue.generate(visitor, context);

        int index = ref.getIndex();
        // store expression at index
        visitor.visitVarInsn(BytecodeUtil.storeInstruction(type), index);
    }
}
