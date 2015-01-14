package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.Typed;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import sun.security.util.Length;

import java.util.ArrayList;
import java.util.List;

public class ArrayInitializerExpr extends Typed implements Expression {

    private Expression component;
    private Expression length;

    public ArrayInitializerExpr(Expression component, Expression length) {
        super(null); // taken care of in overriden methods
        this.component = component;
        this.length = length;
    }

    @Override
    public UnresolvedType getUnresolvedType() {
        if (component instanceof IdentExpr) {
            String name = ((IdentExpr) component).getIdentifier();
            return new UnresolvedType(name, 1);
        } else if (component instanceof ArrayInitializerExpr) {
            Typed componentType = (Typed) component;
            UnresolvedType type = componentType.getUnresolvedType();

            return new UnresolvedType(type.getBaseName(), type.getArrayDimensions() + 1);
        } else {
            throw new RuntimeException("Expected identifier or array initializer");
        }
    }

    public boolean isMultidimensional() {
        return component instanceof ArrayInitializerExpr;
    }

    public int getDimensions() {
        int dims = 1;
        Expression left = component;
        while (left instanceof ArrayInitializerExpr) {
            dims++;
            left = ((ArrayInitializerExpr) left).component;
        }

        return dims;
    }

    private List<Expression> getLengths() {
        List<Expression> lens = new ArrayList<>();
        lens.add(length);

        Expression left = component;
        while (left instanceof ArrayInitializerExpr) {
            Expression len = ((ArrayInitializerExpr) left).length;
            lens.add(len);

            left = ((ArrayInitializerExpr) left).component;
        }

        return lens;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        int dims = getDimensions();
        if (dims < 2) {
            Type elemType = BytecodeUtil.componentType(expressionType());
            BytecodeUtil.createArray(visitor, context, elemType, length);
        } else {
            for (Expression expr : getLengths())
                expr.generate(visitor, context);

            visitor.visitMultiANewArrayInsn(expressionType().getDescriptor(), dims);
        }

    }

    @Override
    public Type expressionType() {
        return getResolvedType();
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[]{length};
    }

}
