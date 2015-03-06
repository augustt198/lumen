package me.august.lumen.compile.ast.stmt;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.ast.CodeBlock;
import me.august.lumen.compile.ast.expr.Expression;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ReturnStmt implements CodeBlock, VisitorConsumer {

    private Expression value;

    public ReturnStmt(Expression value) {
        this.value = value;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        int opcode;
        if (value != null) {
            value.generate(visitor, context);
            opcode = BytecodeUtil.returnOpcode(value.expressionType());
        } else {
            opcode = Opcodes.RETURN;
        }

        visitor.visitInsn(opcode);
    }

    @Override
    public void accept(ASTVisitor astVisitor) {
        if (value != null)
            value.accept(astVisitor);

        astVisitor.visitReturn(this);
    }
}
