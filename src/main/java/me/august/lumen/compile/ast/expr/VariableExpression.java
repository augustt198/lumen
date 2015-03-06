package me.august.lumen.compile.ast.expr;

import me.august.lumen.compile.analyze.var.VariableReference;

public interface VariableExpression extends Expression {

    VariableReference getVariableReference();

}
