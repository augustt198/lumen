package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class StringExpr extends TerminalExpression {

    private String string;
    private char quoteType;

    public StringExpr(String string, char quoteType) {
        this.string = string;
        this.quoteType = quoteType;
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

    public boolean canBeChar() {
        return string.length() == 1 && quoteType == '\'';
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        // load `string` constant
        visitor.visitLdcInsn(string);
    }

    @Override
    public Type expressionType() {
        return BytecodeUtil.STRING_TYPE;
    }
}
