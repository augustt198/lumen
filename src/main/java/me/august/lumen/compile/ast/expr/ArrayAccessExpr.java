package me.august.lumen.compile.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.analyze.var.VariableReference;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

public class ArrayAccessExpr implements Expression, VariableExpression, VariableReference {

    private Expression target;
    private Expression index;

    public ArrayAccessExpr(Expression target, Expression index) {
        this.target = target;
        this.index = index;
    }

    @Override
    public Type expressionType() {
        if (target == null || target.expressionType() == null)
            return null;

        return BytecodeUtil.componentType(target.expressionType());
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        target.generate(visitor, context);
        index.generate(visitor, context);

        int opcode = BytecodeUtil.arrayLoadOpcode(expressionType());
        visitor.visitInsn(opcode);
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{target, index};
    }

    @Override
    public VariableReference getVariableReference() {
        return this;
    }

    @Override
    public Type getType() {
        return expressionType();
    }

    @Override
    public void generateGetCode(MethodVisitor m) {
        generate(m, null);
    }

    @Override
    public void generateSetCode(MethodVisitor m, Consumer<MethodVisitor> insn) {
        target.generate(m, null);
        index.generate(m, null);
        insn.accept(m);

        // stack = array ref, index, value

        int opcode = BytecodeUtil.arrayStoreOpcode(expressionType());
        m.visitInsn(opcode);
    }

    public Expression getIndex() {
        return index;
    }

    public Expression getTarget() {
        return target;
    }
}
