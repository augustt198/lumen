package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class StringExpr extends TerminalExpression {

    private static final Type STRING_TYPE = Type.getType(String.class);

    private String string;

    public StringExpr(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return "StringExpr{" +
            "string='" + string + '\'' +
            '}';
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        // load `string` constant
        visitor.visitLdcInsn(string);
    }

    @Override
    public Type expressionType() {
        return STRING_TYPE;
    }
}
