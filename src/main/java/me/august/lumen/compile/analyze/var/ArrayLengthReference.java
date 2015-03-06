package me.august.lumen.compile.analyze.var;

import me.august.lumen.compile.ast.expr.Expression;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

public class ArrayLengthReference implements VariableReference {

    Expression expr;

    public ArrayLengthReference(Expression expr) {
        this.expr = expr;
    }

    @Override
    public Type getType() {
        return Type.INT_TYPE;
    }

    @Override
    public void generateGetCode(MethodVisitor m) {
        expr.generate(m, null);
        m.visitInsn(Opcodes.ARRAYLENGTH);
    }

    @Override
    public void generateSetCode(MethodVisitor m, Consumer<MethodVisitor> insn) {
        throw new RuntimeException("Array length cannot be set");
    }
}
