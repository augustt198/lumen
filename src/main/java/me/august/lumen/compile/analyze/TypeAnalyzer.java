package me.august.lumen.compile.analyze;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.ast.expr.*;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.resolve.convert.ConversionStrategy;
import me.august.lumen.compile.resolve.convert.Conversions;
import me.august.lumen.compile.resolve.convert.types.PrimitiveWidening;
import me.august.lumen.compile.resolve.convert.types.UnboxingConversion;
import me.august.lumen.compile.resolve.lookup.ClassLookup;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * NOTE: this visitor should be called from
 * the BOTTOM-UP.
 */
public class TypeAnalyzer extends ASTAnnotator<Type> implements ExpressionVisitor {

    private static final Type STRING_TYPE = Type.getType(String.class);

    private Map<Expression, ConversionStrategy> conversions
            = new HashMap<>();

    private ClassLookup lookup;
    private BuildContext build;

    public TypeAnalyzer(ClassLookup lookup, BuildContext build) {
        this.lookup = lookup;
        this.build  = build;
    }

    public void visitNumberExpression(NumExpr expr) {
        Type type = BytecodeUtil.unboxedNumberType(expr.getValue().getClass());
        setValue(expr, type);
    }

    @Override
    public void visitStringExpression(StringExpr expr) {
        setValue(expr, STRING_TYPE);
    }

    @Override
    public void visitTrueExpression(TrueExpr expr) {
        setValue(expr, Type.BOOLEAN_TYPE);
    }

    @Override
    public void visitFalseExpression(FalseExpr expr) {
        setValue(expr, Type.BOOLEAN_TYPE);
    }

    @Override
    public void visitIdentExpression(IdentExpr expr) {
        if (expr.getOwner() == null) {
            Type type = expr.getVariableReference().getType();
            setValue(expr, type);
        }
    }

    @Override
    public void visitAddExpression(AddExpr expr) {
        // String concatenation
        if (expr.isAddition() &&
                getValue(expr.getLeft()).equals(STRING_TYPE) ||
                getValue(expr.getRight()).equals(STRING_TYPE)) {
            setValue(expr, STRING_TYPE);
        } else {
            handleBinaryArithmetic(expr);
        }
    }

    @Override
    public void visitMultExpression(MultExpr expr) {
        handleBinaryArithmetic(expr);
    }

    private void handleBinaryArithmetic(BinaryExpression expr) {
        Type type = applyBinaryNumericPromotion(
                expr.getLeft(), expr.getRight()
        );
        setValue(expr, type);
    }

    private Type applyBinaryNumericPromotion(Expression left, Expression right) {
        Type lType  = getValue(left);
        Type rType = getValue(right);

        ConversionStrategy lConversions  = new ConversionStrategy();
        ConversionStrategy rConversions = new ConversionStrategy();
        if (BytecodeUtil.isNumericBoxedType(lType)) {
            Type unboxed = BytecodeUtil.unboxedNumberType(lType);
            lConversions.addStep(new UnboxingConversion(lType, unboxed));
            lType = unboxed;
        }
        if (BytecodeUtil.isNumericBoxedType(rType)) {
            Type unboxed = BytecodeUtil.unboxedNumberType(rType);
            rConversions.addStep(new UnboxingConversion(rType, unboxed));
            rType = unboxed;
        }

        int compare = BytecodeUtil.compareWidth(lType, rType);
        // left is wider
        if (compare > 0) {
            rConversions.addStep(new PrimitiveWidening(rType, lType));
            rType = lType;

        // right is wider
        } else if (compare < 0) {
            lConversions.addStep(new PrimitiveWidening(lType, rType));
        }

        // rType == lType
        return rType;
    }

    @Override
    public void visitBitAndExpression(BitAndExpr expr) {
        setValue(expr, handleBinaryBitwise(expr));
    }

    @Override
    public void visitBitOrExpression(BitOrExpr expr) {
        setValue(expr, handleBinaryBitwise(expr));
    }

    @Override
    public void visitBitXorExpression(BitXorExpr expr) {
        setValue(expr, handleBinaryBitwise(expr));
    }

    private Type handleBinaryBitwise(BinaryExpression expr) {
        Type lType = getValue(expr.getLeft());
        Type rType = getValue(expr.getRight());

        Type type = Type.INT_TYPE;
        if (lType == Type.LONG_TYPE || rType == Type.LONG_TYPE) {
            type = Type.LONG_TYPE;
        }

        if (lType != type) {
            ConversionStrategy conversion = new ConversionStrategy();
            conversion.addStep(new PrimitiveWidening(lType, type));
            conversions.put(expr.getLeft(), conversion);
        }
        if (rType != type) {
            ConversionStrategy conversion = new ConversionStrategy();
            conversion.addStep(new PrimitiveWidening(rType, type));
            conversions.put(expr.getRight(), conversion);
        }

        return type;
    }

    // shift expressions are a bit different than
    // the other bitwise operators because the
    // type relies soley on the type of the left
    // expression, i.e. "1 << 1L" still has the
    // int type.
    @Override
    public void visitShiftExpression(ShiftExpr expr) {
        Type type = Type.INT_TYPE;
        if (getValue(expr.getLeft()) == Type.LONG_TYPE) {
            type = Type.LONG_TYPE;
        }

        // if right expression is of type long,
        // convert to int
        Type rType = getValue(expr.getRight());
        if (rType == Type.LONG_TYPE) {
            ConversionStrategy conversion = new ConversionStrategy();
            // it's not actually widening... but the
            // PrimitiveWidening class doesn't care
            // (it probably should)
            // TODO ^^^^^^^^^^^^^^^
            conversion.addStep(new PrimitiveWidening(rType, Type.INT_TYPE));
        }

        setValue(expr, type);
    }

    @Override
    public void visitAssignmentExpression(AssignmentExpr expr) {
        Type varType = expr.getLeft().getVariableReference().getType();
        Type valType = getValue(expr.getRight());

        ConversionStrategy conversion = Conversions.convert(
                valType, varType, lookup
        );

        if (conversion.isValid()) {
            conversions.put(expr.getRight(), conversion);
        } else {
            build.error(
                    "Cannot assign " + valType + " to " + varType,
                    true, expr
            );
        }
    }

    @Override
    public void visitArrayAccessExpression(ArrayAccessExpr expr) {
        // type of x in x[foo]
        Type targetType = getValue(expr.getTarget());
        Type componentType = BytecodeUtil.componentType(targetType);

        setValue(expr, componentType);
    }

    @Override
    public void visitBitwiseComplementExpression(BitwiseComplementExpr expr) {
        handleNumericalUnary(expr);
    }

    @Override
    public void visitUnaryMinusExpression(UnaryMinusExpr expr) {
        handleNumericalUnary(expr);
    }

    private void handleNumericalUnary(UnaryExpression expr) {
        Type operandType = getValue(expr.getOperand());

        Type targetType = Type.INT_TYPE;
        if (operandType == Type.LONG_TYPE) {
            targetType = Type.LONG_TYPE;
        }

        setValue(expr, targetType);
    }

    @Override
    public void visitIncrementExpression(IncrementExpr expr) {
        Type operandType = getValue(expr.getOperand());
        setValue(expr, operandType);
    }

    @Override
    public void visitNotExpression(NotExpr expr) {
        handleLogicExpression(expr);
    }

    @Override
    public void visitAndExpression(AndExpr expr) {
        handleLogicExpression(expr);
    }

    @Override
    public void visitOrExpression(OrExpr expr) {
        handleLogicExpression(expr);
    }

    private void handleLogicExpression(Expression expr) {
        setValue(expr, Type.BOOLEAN_TYPE);
    }

}
