package me.august.lumen.compile.ast.expr;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.ast.SingleTypedNode;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.resolve.type.BasicType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.List;

public class ArrayInitializerExpr extends SingleTypedNode implements Expression {

    private List<Expression> lengths;
    private int dims;

    public ArrayInitializerExpr(BasicType type, List<Expression> lengths, int dims) {
        // turn base type into array type
        super(new BasicType(type.getBaseName(), dims));
        this.lengths  = lengths;
        this.dims     = dims;
    }

    public boolean isMultidimensional() {
        return dims > 1;
    }


    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        if (isMultidimensional()) {
            for (Expression len : lengths) {
                len.generate(visitor, context);
            }
            visitor.visitMultiANewArrayInsn(expressionType().getDescriptor(), lengths.size());
        } else {
            Type elemType = BytecodeUtil.componentType(expressionType());
            BytecodeUtil.createArray(visitor, context, elemType, lengths.get(0));
        }

    }

    @Override
    public Type expressionType() {
        return getResolvedType();
    }

    @Override
    public Expression[] getChildren() {
        return lengths.toArray(new Expression[lengths.size()]);
    }

}
