package me.august.lumen.compile.parser.ast.stmt;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.CodeBlock;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class NextStmt implements CodeBlock, VisitorConsumer {

    private Loop owner;

    public NextStmt() {
    }

    public NextStmt(Loop owner) {
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
        visitor.visitJumpInsn(Opcodes.GOTO, owner.getRepeatLabel());
    }

    @Override
    public void accept(ASTVisitor astVisitor) {
        astVisitor.visitNextStmt(this);
    }
}
