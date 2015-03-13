package me.august.lumen.compile.ast.expr;

import me.august.lumen.compile.ast.SingleTypedNode;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.resolve.type.BasicType;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class RescueExpr extends SingleTypedNode implements Expression {

    private Expression tryExpression;
    private Expression catchExpression;

    private int exceptionVariableIndex;

    public RescueExpr(BasicType type, Expression tryExpression, Expression catchExpression) {
        super(type);
        this.tryExpression = tryExpression;
        this.catchExpression = catchExpression;
    }

    public int getExceptionVariableIndex() {
        return exceptionVariableIndex;
    }

    public void setExceptionVariableIndex(int exceptionVariableIndex) {
        this.exceptionVariableIndex = exceptionVariableIndex;
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{tryExpression, catchExpression};
    }

    @Override
    public Type expressionType() {
        return tryExpression.expressionType();
    }

    // TODO fix codegen
    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        Label start  = new Label();
        Label end    = new Label();
        Label handle = new Label();

        visitor.visitTryCatchBlock(start, end, handle, getResolvedType().getInternalName());

        visitor.visitLabel(start);
        tryExpression.generate(visitor, context);
        visitor.visitLabel(end);

        Label finish = new Label();
        visitor.visitJumpInsn(Opcodes.GOTO, finish);

        visitor.visitLabel(handle);
        visitor.visitVarInsn(Opcodes.ASTORE, exceptionVariableIndex);
        catchExpression.generate(visitor, context);

        visitor.visitLabel(finish);
    }

    @Override
    public String toString() {
        return "RescueExpr{" +
            "tryExpression=" + tryExpression +
            ", catchExpression=" + catchExpression +
            '}';
    }
}
