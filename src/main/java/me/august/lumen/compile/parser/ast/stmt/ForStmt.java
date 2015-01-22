package me.august.lumen.compile.parser.ast.stmt;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.parser.ast.expr.RangeExpr;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ForStmt implements CodeBlock, VisitorConsumer, Loop {

    private String ident;
    private RangeExpr range;
    private Body body;

    private Label repeat;
    private Label exit;

    private int counterIndex;
    private int boundIndex;

    public ForStmt(String ident, RangeExpr range, Body body) {
        this.ident = ident;
        this.range = range;
        this.body  = body;
    }

    @Override
    public Label getRepeatLabel() {
        return repeat;
    }

    @Override
    public Label getExitLabel() {
        return exit;
    }

    public String getIdent() {
        return ident;
    }

    public void setCounterIndex(int counterIndex) {
        this.counterIndex = counterIndex;
    }

    public void setBoundIndex(int boundIndex) {
        this.boundIndex = boundIndex;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        repeat = new Label();
        exit   = new Label();

        range.getLeft().generate(visitor, context);
        visitor.visitVarInsn(Opcodes.ISTORE, counterIndex);

        range.getRight().generate(visitor, context);
        visitor.visitVarInsn(Opcodes.ISTORE, boundIndex);

        visitor.visitLabel(repeat);

        visitor.visitVarInsn(Opcodes.ILOAD, counterIndex);
        visitor.visitVarInsn(Opcodes.ILOAD, boundIndex);

        int opcode = range.isExclusive() ? Opcodes.IF_ICMPGE
                : Opcodes.IF_ICMPGT;
        visitor.visitJumpInsn(opcode, exit);

        body.generate(visitor, context);

        visitor.visitIincInsn(counterIndex, 1);
        visitor.visitJumpInsn(Opcodes.GOTO, repeat);

        visitor.visitLabel(exit);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        range.getLeft().accept(visitor);
        range.getRight().accept(visitor);
        visitor.visitForStmt(this);

        body.accept(visitor);
    }
}
