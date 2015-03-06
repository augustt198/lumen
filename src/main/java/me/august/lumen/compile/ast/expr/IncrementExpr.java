package me.august.lumen.compile.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.analyze.var.LocalVariable;
import me.august.lumen.compile.analyze.var.VariableReference;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

// We're pretending to use the POP instruction, because
// the same rules apply to incrementing.
public class IncrementExpr implements Expression {

    public enum Op {
        INC(1), DEC(-1);

        Op(int delta) {
            this.delta = delta;
        }

        private int delta;
    }

    private VariableExpression value;
    private Op op;
    private boolean postfix;

    // whether or not the variable getter should be generated:
    // e.g. "foo++" should not generate the variable getter,
    // but "bar = foo++" should generate the variable getter.
    private boolean shouldGen = true;

    public IncrementExpr(Expression value, Op op, boolean postfix) {
        if (!(value instanceof VariableExpression))
            throw new IllegalArgumentException("Invalid type, expected variable");

        this.value   = (VariableExpression) value;
        this.op      = op;
        this.postfix = postfix;
    }

    @Override
    public Type expressionType() {
        return value.expressionType();
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        if (!BytecodeUtil.isNumeric(expressionType()) || expressionType().getSort() > Type.INT) {
            context.error(
                    "Incompatible type: " + expressionType(),
                    false, this
            );
        }

        // If it's postfix we load the value
        // *before* incrementing, so the un-incremented
        // value is pushed onto the stack.
        if (postfix && shouldGen) {
            value.generate(visitor, context);
        }

        // if it's a local variable, use IINC instruction
        VariableReference var = value.getVariableReference();
        if (var instanceof LocalVariable) {
            LocalVariable local = (LocalVariable) var;
            visitor.visitIincInsn(local.getIndex(), op.delta);
        } else { // otherwise: load, add 1, save
            var.generateSetCode(visitor, (m) -> {
                var.generateGetCode(m);
                BytecodeUtil.pushInt(m, op.delta);
                m.visitInsn(Opcodes.IADD);
            });
        }

        // If it's not postfix we load the value
        // *after* incrementing, so the incremented
        // value is pushed onto the stack.
        if (!postfix && shouldGen) {
            value.generate(visitor, context);
        }
    }

    @Override
    public void markAsTopLevelStatement(boolean flag) {
        shouldGen = !flag;
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{value};
    }
}
