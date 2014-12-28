package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.analyze.var.VariableReference;
import me.august.lumen.compile.codegen.BuildContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class IdentExpr extends TerminalExpression implements OwnedExpr {

    private String identifier;
    private Expression owner;

    private VariableReference ref;

    private Type expressionType;

    public IdentExpr(String identifier) {
        this.identifier = identifier;
    }

    public IdentExpr(String identifier, Expression owner) {
        this.identifier = identifier;
        this.owner = owner;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    public String getIdentifier() {
        return identifier;
    }

    public VariableReference getRef() {
        return ref;
    }

    public void setRef(VariableReference ref) {
        this.ref = ref;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        ref.generateGetCode(visitor);
    }

    @Override
    public Expression getOwner() {
        return owner;
    }

    public Type expressionType() {
        return expressionType;
    }

    public void setExpressionType(Type expressionType) {
        this.expressionType = expressionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentExpr identExpr = (IdentExpr) o;

        if (identifier != null ? !identifier.equals(identExpr.identifier) : identExpr.identifier != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "IdentExpr{" +
            "identifier='" + identifier + '\'' +
            ", owner=" + owner +
            ", ref=" + ref +
            '}';
    }
}
