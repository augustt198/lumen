package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.expr.owned.OwnedExpr;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

public class MethodCallExpr extends TerminalExpression implements OwnedExpr {

    private String identifier;
    private List<Expression> params;

    private Expression owner;

    public MethodCallExpr(String identifier, List<Expression> params) {
        this(identifier, params, null);
    }

    public MethodCallExpr(String identifier, List<Expression> params, Expression owner) {
        this.identifier = identifier;
        this.params = params;
        this.owner = owner;
    }

    @Override
    public Expression getOwner() {
        return owner;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        if (getOwner() != null) getOwner().generate(visitor, context);

        // load actual arguments
        for (Expression expr : params) expr.generate(visitor, context);

        visitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            // TODO fix null param (class in which method is found)
            null,
            identifier,
            // TODO ew. We need more information about the method!
            "()V",
            // TODO actually detect interfaces
            false
        );
    }

    @Override
    public String toString() {
        return "MethodCallExpr{" +
            "identifier='" + identifier + '\'' +
            ", params=" + params +
            ", owner=" + owner +
            '}';
    }
}
