package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.analyze.method.MethodReference;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class MethodCallExpr extends TerminalExpression implements OwnedExpr {

    private String identifier;
    private List<Expression> params;

    private Expression owner;

    private MethodReference ref;

    public MethodCallExpr(String identifier, List<Expression> params) {
        this(identifier, params, null);
    }

    public MethodCallExpr(String identifier, List<Expression> params, Expression owner) {
        this.identifier = identifier;
        this.params = params;
        this.owner = owner;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<Expression> getParams() {
        return params;
    }

    public MethodReference getRef() {
        return ref;
    }

    public void setRef(MethodReference ref) {
        this.ref = ref;
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

        ref.generate(visitor, context);
    }

    @Override
    public Expression[] getChildren() {
        return params.toArray(new Expression[params.size()]);
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
