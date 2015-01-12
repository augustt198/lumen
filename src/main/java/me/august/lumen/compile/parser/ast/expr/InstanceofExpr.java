package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.Typed;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

// NOTE: unlike most other expressions, this expression type
// has the *same* precedence as RelExpr. They have been separated
// for ease of use.
public class InstanceofExpr extends Typed implements Expression {

    private Expression value;

    public InstanceofExpr(Expression value, UnresolvedType unresolvedType) {
        super(unresolvedType);
        this.value = value;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        value.generate(visitor, context);

        visitor.visitTypeInsn(Opcodes.INSTANCEOF, getResolvedType().getInternalName());
    }

    @Override
    public Type expressionType() {
        return Type.BOOLEAN_TYPE;
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{value};
    }
}
