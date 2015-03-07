package me.august.lumen.compile.ast.stmt;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.ast.CodeBlock;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class BreakStmt implements CodeBlock, VisitorConsumer {

    private Loop owner;

    public BreakStmt() {
    }

    public BreakStmt(Loop owner) {
        this.owner = owner;
    }

    public Loop getOwner() {
        return owner;
    }

    public void setOwner(Loop owner) {
        this.owner = owner;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        visitor.visitJumpInsn(Opcodes.GOTO, owner.getExitLabel());
    }

    @Override
    public void acceptTopDown(ASTVisitor visitor) {
        visitor.visitBreakStmt(this);
    }

    @Override
    public void acceptBottomUp(ASTVisitor visitor) {
        acceptTopDown(visitor);
    }
}
