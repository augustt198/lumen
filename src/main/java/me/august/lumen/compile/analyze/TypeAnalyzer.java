package me.august.lumen.compile.analyze;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.ast.expr.*;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.resolve.convert.ConversionStrategy;
import me.august.lumen.compile.resolve.convert.Conversions;
import me.august.lumen.compile.resolve.convert.types.PrimitiveWidening;
import me.august.lumen.compile.resolve.convert.types.UnboxingConversion;
import me.august.lumen.compile.resolve.data.ClassData;
import me.august.lumen.compile.resolve.data.FieldData;
import me.august.lumen.compile.resolve.data.MethodData;
import me.august.lumen.compile.resolve.data.exception.AmbiguousMethodException;
import me.august.lumen.compile.resolve.lookup.ClassLookup;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            setValue(expr, valType);
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

    private static final Type BOXED_BOOLEAN_TYPE = Type.getType(Boolean.class);

    private void handleBooleanCondition(Expression expr) {
        Type type = getValue(expr);

        // boxed boolean
        if (type.equals(BOXED_BOOLEAN_TYPE)) {
            ConversionStrategy conversion = new ConversionStrategy();
            conversion.addStep(new UnboxingConversion(type));
            conversions.put(expr, conversion);
            setValue(expr, Type.BOOLEAN_TYPE);
        }
    }

    @Override
    public void visitInstanceofExpression(InstanceofExpr expr) {
        handleLogicExpression(expr);
    }

    @Override
    public void visitArrayInitializerExpression(ArrayInitializerExpr expr) {
        Type type = expr.getTypeInfo().getResolvedType();
        setValue(expr, type);
    }

    @Override
    public void visitEqExpression(EqExpr expr) {
        Type lType = getValue(expr.getLeft());
        Type rType = getValue(expr.getRight());
        ConversionStrategy lConvert = new ConversionStrategy();
        ConversionStrategy rConvert = new ConversionStrategy();

        // if left=numeric, right=boxed numeric or vice-versa
        boolean numeric = true;
        if (BytecodeUtil.isPrimitive(lType) && BytecodeUtil.isNumericBoxedType(rType)) {
            Type unboxed = BytecodeUtil.unboxedNumberType(rType);
            rConvert.addStep(new UnboxingConversion(rType, unboxed));
            rType = unboxed;
        } else if (BytecodeUtil.isNumericBoxedType(lType) && BytecodeUtil.isNumeric(rType)) {
            Type unboxed = BytecodeUtil.unboxedNumberType(lType);
            lConvert.addStep(new UnboxingConversion(lType, unboxed));
            lType = unboxed;
        } else {
            numeric = false;
        }

        if (numeric) {
            int cmp = BytecodeUtil.compareWidth(lType, rType);

            if (cmp > 0) {
                lConvert.addStep(new PrimitiveWidening(rType, lType));
            } else if (cmp < 0) {
                lConvert.addStep(new PrimitiveWidening(lType, rType));
            }
        }

        conversions.put(expr.getLeft(), lConvert);
        conversions.put(expr.getRight(), rConvert);

        handleLogicExpression(expr);
    }

    @Override
    public void visitRelExpression(RelExpr expr) {
        Type lType = getValue(expr.getLeft());
        Type rType = getValue(expr.getRight());
        ConversionStrategy lConvert = new ConversionStrategy();
        ConversionStrategy rConvert = new ConversionStrategy();

        if (BytecodeUtil.isNumericBoxedType(lType)) {
            Type unboxed = BytecodeUtil.unboxedNumberType(lType);
            lConvert.addStep(new UnboxingConversion(lType, unboxed));
            lType = unboxed;
        }
        if (BytecodeUtil.isNumericBoxedType(rType)) {
            Type unboxed = BytecodeUtil.unboxedNumberType(rType);
            rConvert.addStep(new UnboxingConversion(rType, unboxed));
            rType = unboxed;
        }

        if (!BytecodeUtil.isNumeric(lType) || !BytecodeUtil.isNumeric(rType)) {
            build.error(
                    "Uncompatible types: " + lType + ", " + rType,
                    true, expr
            );
        }


        int cmp = BytecodeUtil.compareWidth(lType, rType);
        if (cmp > 0) {
            lConvert.addStep(new PrimitiveWidening(rType, lType));
        } else if (cmp < 0) {
            lConvert.addStep(new PrimitiveWidening(lType, rType));
        }

        conversions.put(expr.getLeft(), lConvert);
        conversions.put(expr.getRight(), rConvert);

        handleLogicExpression(expr);
    }

    @Override
    public void visitOwnedExpression(OwnedExpr expr) {
        if (expr.getOwner() != null) {
            visitExpression(expr.getOwner());
        }
    }

    @Override
    public void visitIdentExpression(IdentExpr expr) {
        if (expr.getOwner() == null) {
            Type type = expr.getVariableReference().getType();
            setValue(expr, type);
        } else {
            Type ownerType = getValue(expr.getOwner());
            ClassData data = lookup.lookup(ownerType);
            if (data == null) {
                build.error(
                        "Undefined class: " + ownerType,
                        true, expr
                );
                return; // unreachable
            }

            FieldData field = data.getField(expr.getIdentifier());
            if (field == null) {
                build.error(
                        "Undefined instance variable: " + expr.getIdentifier(),
                        true, expr
                );
                return; // unreachable
            }

            setValue(expr, field.getType());
        }
    }

    @Override
    public void visitStaticFieldExpression(StaticField expr) {
        Type classType = expr.getTypeInfo().getResolvedType();
        ClassData data = lookup.lookup(classType);

        if (data == null) {
            build.error(
                    "Undefined class: " + classType,
                    true, expr
            );
            return; // unreachable
        }

        FieldData field = data.getField(expr.getFieldName());
        if (field == null) {
            build.error(
                    "Undefined static variable: " + expr.getFieldName(),
                    true, expr
            );
            return; // unreachable
        }

        setValue(expr, field.getType());
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpr expr) {
        Type ownerType = getValue(expr.getOwner());

        ClassData data = lookup.lookup(ownerType);
        if (data == null) {
            build.error(
                    "Undefined class: " + ownerType,
                    true, expr
            );
            return; // unreachable
        }

        List<Type> types = expr.getParams()
                .stream()
                .map(this::getValue)
                .collect(Collectors.toList());

        try {
            MethodData method = Conversions.pickMethod(
                    types,
                    data.getMethods(expr.getIdentifier()), lookup
            );

            setValue(expr, method.getReturnType());
        } catch (AmbiguousMethodException e) {
            build.error(
                    "Ambiguous method call",
                    true, expr
            );
        }
    }

    @Override
    public void visitTernaryExpression(TernaryExpr expr) {
        handleBooleanCondition(expr.getCondition());

        Type retType = getValue(expr.getTrueExpr());
        setValue(expr, retType);
    }
}
