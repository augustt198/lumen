package me.august.lumen.compile.ast.expr;

import me.august.lumen.compile.ast.SingleTypedNode;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.resolve.type.BasicType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;

public class ConstructorCallExpr extends SingleTypedNode implements Expression {

    private List<Expression> params;

    public ConstructorCallExpr(BasicType unresolvedType, List<Expression> params) {
        super(unresolvedType);
        this.params = params;
    }

    @Override
    public Expression[] getChildren() {
        return params.toArray(new Expression[params.size()]);
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        String internalName = getTypeInfo().getResolvedType().getInternalName();

        visitor.visitTypeInsn(Opcodes.NEW, internalName);
        visitor.visitInsn(Opcodes.DUP);

        Type[] paramTypes = new Type[params.size()];
        for (int i = 0; i < params.size(); i++)
            paramTypes[i] = params.get(i).expressionType();

        Type methodType = Type.getMethodType(Type.VOID_TYPE, paramTypes);

        for (Expression param : params)
            param.generate(visitor, context);

        visitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            internalName,
            "<init>",
            methodType.getDescriptor(),
            false
        );
    }

    @Override
    public Type expressionType() {
        return getTypeInfo().getResolvedType();
    }
}
