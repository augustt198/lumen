package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.analyze.var.VariableReference;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;

public class AssignmentExpr extends BinaryExpression {

    // BinaryExpression only defines left as general expression
    VariableExpression left;

    public AssignmentExpr(VariableExpression left, Expression right) {
        super(left, right);
        this.left = left;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        left.getVariableReference().generateSetCode(visitor, (m) -> right.generate(visitor, context));
    }
}
