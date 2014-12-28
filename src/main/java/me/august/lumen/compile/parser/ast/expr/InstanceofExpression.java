package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.Typed;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

// NOTE: unlike most other expressions, this expression type
// has the *same* precedence as RelExpr. They have been separated
// for ease of use.
public class InstanceofExpression extends Typed implements Expression {

    private Expression value;

    public InstanceofExpression(Expression value, String simpleType) {
        super(simpleType);
        this.value = value;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        value.generate(visitor, context);

        visitor.visitTypeInsn(Opcodes.INSTANCEOF, getResolvedType().getInternalName());
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{value};
    }
}
