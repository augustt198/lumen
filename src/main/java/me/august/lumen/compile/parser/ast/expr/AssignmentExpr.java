package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.scanner.Op;
import org.objectweb.asm.MethodVisitor;

public class AssignmentExpr extends BinaryExpression {

    // BinaryExpression only defines left as general expression
    IdentExpr left;

    public AssignmentExpr(IdentExpr left, Expression right) {
        super(left, right, Op.ASSIGN);
        this.left = left;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        left.getRef().generateSetCode(visitor, (m) -> right.generate(visitor, context));
    }
}
