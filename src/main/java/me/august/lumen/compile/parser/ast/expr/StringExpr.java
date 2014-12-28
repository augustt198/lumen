package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.scanner.tokens.StringToken;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class StringExpr extends TerminalExpression {

    private String string;
    private StringToken.QuoteType quoteType;

    public StringExpr(String string, StringToken.QuoteType quoteType) {
        this.string = string;
        this.quoteType = quoteType;
    }

    @Override
    public String toString() {
        return "StringExpr{" +
            "string='" + string + '\'' +
            ", quoteType=" + quoteType.name() +
            '}';
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    public boolean canBeChar() {
        return string.length() == 1 && quoteType.equals(StringToken.QuoteType.SINGLE);
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
