package me.august.lumen.compile.ast.expr;

import me.august.lumen.compile.codegen.Branch;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.Conditional;
import me.august.lumen.compile.codegen.MethodCodeGen;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;


public class TernaryExpr implements Expression, Conditional {

    private Expression condition;

    private Expression trueExpr;
    private Expression falseExpr;

    public TernaryExpr(Expression condition, Expression trueExpr, Expression falseExpr) {
        this.condition = condition;
        this.trueExpr = trueExpr;
        this.falseExpr = falseExpr;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getTrueExpr() {
        return trueExpr;
    }

    public Expression getFalseExpr() {
        return falseExpr;
    }

    @Override
    public Type expressionType() {
        return trueExpr.expressionType();
    }

    @Override
    public Branch branch(MethodCodeGen ifBranch, MethodCodeGen elseBranch) {
        if (condition instanceof Conditional) {
            return ((Conditional) condition).branch(ifBranch, elseBranch);
        } else if (condition.expressionType().getSort() == Type.BOOLEAN) {
            return Branch.branchBoolean(condition, ifBranch, elseBranch);
        } else {
            throw new IllegalArgumentException("Incompatible types.");
        }
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        branch(trueExpr, falseExpr).generate(visitor, context);
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{condition, trueExpr, falseExpr};
    }

    @Override
    public boolean isConstant() {
        return condition.isConstant() && trueExpr.isConstant() && falseExpr.isConstant();
    }

    @Override
    public String toString() {
        return "TernaryExpr{" +
            "condition=" + condition +
            ", trueExpr=" + trueExpr +
            ", falseExpr=" + falseExpr +
            '}';
    }
}
