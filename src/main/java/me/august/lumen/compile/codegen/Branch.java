package me.august.lumen.compile.codegen;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Branch implements MethodCodeGen {

    private MethodCodeGen cond;

    private Label elseJump;

    private MethodCodeGen ifBranch;
    private MethodCodeGen elseBranch;

    public Branch(MethodCodeGen cond, Label elseJump, MethodCodeGen ifBranch, MethodCodeGen elseBranch) {
        this.cond = cond;
        this.elseJump = elseJump;
        this.ifBranch = ifBranch;
        this.elseBranch = elseBranch;
    }

    public MethodCodeGen getCond() {
        return cond;
    }

    public void setCond(MethodCodeGen cond) {
        this.cond = cond;
    }

    public Label getElseJump() {
        return elseJump;
    }

    public void setElseJump(Label elseJump) {
        this.elseJump = elseJump;
    }

    public MethodCodeGen getIfBranch() {
        return ifBranch;
    }

    public void setIfBranch(MethodCodeGen ifBranch) {
        this.ifBranch = ifBranch;
    }

    public MethodCodeGen getElseBranch() {
        return elseBranch;
    }

    public void setElseBranch(MethodCodeGen elseBranch) {
        this.elseBranch = elseBranch;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        cond.generate(visitor, context);
        ifBranch.generate(visitor, context);

        Label avoidElse = null;
        if (elseBranch != null) {
            avoidElse = new Label();
            visitor.visitJumpInsn(Opcodes.GOTO, avoidElse);
        }

        visitor.visitLabel(elseJump);

        if (elseBranch != null) {
            elseBranch.generate(visitor, context);
            visitor.visitLabel(avoidElse);
        }
    }

}
