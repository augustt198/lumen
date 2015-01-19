package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.Typed;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;

public class ConstructorCallExpr extends Typed implements Expression {

    private List<Expression> params;

    public ConstructorCallExpr(UnresolvedType unresolvedType, List<Expression> params) {
        super(unresolvedType);
        this.params = params;
    }

    @Override
    public Expression[] getChildren() {
        return params.toArray(new Expression[params.size()]);
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        visitor.visitTypeInsn(Opcodes.NEW, getResolvedType().getInternalName());
        visitor.visitInsn(Opcodes.DUP);

        Type[] paramTypes = new Type[params.size()];
        for (int i = 0; i < params.size(); i++)
            paramTypes[i] = params.get(i).expressionType();

        Type methodType = Type.getMethodType(Type.VOID_TYPE, paramTypes);

        visitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            getResolvedType().getInternalName(),
            "<init>",
            methodType.getDescriptor(),
            false
        );
    }

    @Override
    public Type expressionType() {
        return getResolvedType();
    }
}
