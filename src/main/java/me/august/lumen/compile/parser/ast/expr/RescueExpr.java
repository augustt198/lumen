package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.Typed;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class RescueExpr extends Typed implements Expression {

    private Expression tryExpression;
    private Expression catchExpression;

    public RescueExpr(UnresolvedType type, Expression tryExpression, Expression catchExpression) {
        super(type);
        this.tryExpression = tryExpression;
        this.catchExpression = catchExpression;
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
