package me.august.lumen.compile.ast.stmt;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.ast.CodeBlock;
import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class EachStmt implements CodeBlock, VisitorConsumer, Loop {

    private Label repeat;
    private Label exit;

    private String ident;
    private Expression expr;
    private Body body;

    private int counterIndex;
    private int arrayIndex;
    private int targetIndex;

    public EachStmt(String ident, Expression expr, Body body) {
        this.ident = ident;
        this.expr = expr;
        this.body = body;
    }

    public Label getRepeatLabel() {
        return repeat;
    }

    public Label getExitLabel() {
        return exit;
    }

    public void setCounterIndex(int counterIndex) {
        this.counterIndex = counterIndex;
    }

    public void setArrayIndex(int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        repeat = new Label();
        exit   = new Label();

        visitor.visitInsn(Opcodes.ICONST_0);
        visitor.visitVarInsn(Opcodes.ISTORE, counterIndex);
        expr.generate(visitor, context);
        visitor.visitVarInsn(
                BytecodeUtil.storeInstruction(expr.expressionType()), arrayIndex
        );


        visitor.visitLabel(repeat);
        visitor.visitVarInsn(Opcodes.ILOAD, counterIndex);
        visitor.visitVarInsn(
                BytecodeUtil.loadInstruction(expr.expressionType()), arrayIndex
        );
        visitor.visitInsn(Opcodes.ARRAYLENGTH);
        visitor.visitJumpInsn(Opcodes.IF_ICMPGE, exit);

        Type type = BytecodeUtil.componentType(expr.expressionType());
        expr.generate(visitor, context);
        visitor.visitVarInsn(Opcodes.ILOAD, counterIndex);
        visitor.visitInsn(BytecodeUtil.arrayLoadOpcode(type));
        visitor.visitVarInsn(BytecodeUtil.storeInstruction(type), targetIndex);

        body.generate(visitor, context);

        visitor.visitIincInsn(counterIndex, 1);
        visitor.visitJumpInsn(Opcodes.GOTO, repeat);

        visitor.visitLabel(exit);
    }

    @Override
    public void acceptTopDown(ASTVisitor visitor) {
        visitor.visitEachStmt(this);

        expr.acceptTopDown(visitor);
        body.acceptTopDown(visitor, false);
    }

    @Override
    public void acceptBottomUp(ASTVisitor visitor) {
        visitor.visitEachStmt(this);

        expr.acceptTopDown(visitor);
        body.acceptTopDown(visitor, false);
    }

    public String getIdent() {
        return ident;
    }

    public Expression getExpr() {
        return expr;
    }
}
