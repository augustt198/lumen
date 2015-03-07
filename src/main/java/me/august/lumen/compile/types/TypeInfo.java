package me.august.lumen.compile.types;

import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import org.objectweb.asm.Type;

public class TypeInfo {

    private Expression expression;

    private UnresolvedType originalType;
    private Type targetType;

    public TypeInfo(Expression expression, UnresolvedType originalType, Type targetType) {
        this.expression = expression;
        this.originalType = originalType;
        this.targetType = targetType;
    }

    public TypeInfo(Expression expression, UnresolvedType originalType) {
        this(expression, originalType, null);
    }

    public Expression getExpression() {
        return expression;
    }

    public UnresolvedType getOriginalType() {
        return originalType;
    }

    public Type getTargetType() {
        return targetType;
    }

    public void setTargetType(Type targetType) {
        this.targetType = targetType;
    }
}
