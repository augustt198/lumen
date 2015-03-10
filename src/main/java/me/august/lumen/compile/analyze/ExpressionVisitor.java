package me.august.lumen.compile.analyze;

import me.august.lumen.compile.ast.expr.*;

// java pls
public interface ExpressionVisitor extends ASTVisitor {

    @Override
    default void visitExpression(Expression expr) {
        if (expr instanceof OwnedExpr)
            visitOwnedExpression((OwnedExpr) expr);
        if (expr instanceof BinaryExpression)
            visitBinaryExpression((BinaryExpression) expr);
        if (expr instanceof TerminalExpression)
            visitTerminalExpression((TerminalExpression) expr);

        if (expr instanceof CastExpr)
            visitCastExpression((CastExpr) expr);
        else if (expr instanceof StaticMethodCall)
            visitStaticMethodCallExpression((StaticMethodCall) expr);
        else if (expr instanceof TrueExpr)
            visitTrueExpression((TrueExpr) expr);
        else if (expr instanceof FalseExpr)
            visitFalseExpression((FalseExpr) expr);
        else if (expr instanceof NumExpr)
            visitNumberExpression((NumExpr) expr);
        else if (expr instanceof StringExpr)
            visitStringExpression((StringExpr) expr);
        else if (expr instanceof IdentExpr)
            visitIdentExpression((IdentExpr) expr);
        else if (expr instanceof NullExpr)
            visitNullExpression((NullExpr) expr);
        else if (expr instanceof ArrayAccessExpr)
            visitArrayAccessExpression((ArrayAccessExpr) expr);
        else if (expr instanceof StaticField)
            visitStaticFieldExpression((StaticField) expr);
        else if (expr instanceof NotExpr)
            visitNotExpression((NotExpr) expr);
        else if (expr instanceof ConstructorCallExpr)
            visitConstructorExpression((ConstructorCallExpr) expr);
        else if (expr instanceof MethodCallExpr)
            visitMethodCallExpression((MethodCallExpr) expr);
        else if (expr instanceof AndExpr)
            visitAndExpression((AndExpr) expr);
        else if (expr instanceof EqExpr)
            visitEqExpression((EqExpr) expr);
        else if (expr instanceof OrExpr)
            visitOrExpression((OrExpr) expr);
        else if (expr instanceof AssignmentExpr)
            visitAssignmentExpression((AssignmentExpr) expr);
        else if (expr instanceof RangeExpr)
            visitRangeExpression((RangeExpr) expr);
        else if (expr instanceof ShiftExpr)
            visitShiftExpression((ShiftExpr) expr);
        else if (expr instanceof AddExpr)
            visitAddExpression((AddExpr) expr);
        else if (expr instanceof RelExpr)
            visitRelExpression((RelExpr) expr);
        else if (expr instanceof BitXorExpr)
            visitBitXorExpression((BitXorExpr) expr);
        else if (expr instanceof BitOrExpr)
            visitBitOrExpression((BitOrExpr) expr);
        else if (expr instanceof MultExpr)
            visitMultExpression((MultExpr) expr);
        else if (expr instanceof BitAndExpr)
            visitBitAndExpression((BitAndExpr) expr);
        else if (expr instanceof ArrayInitializerExpr)
            visitArrayInitializerExpression((ArrayInitializerExpr) expr);
        else if (expr instanceof IncrementExpr)
            visitIncrementExpression((IncrementExpr) expr);
        else if (expr instanceof RescueExpr)
            visitRescueExpression((RescueExpr) expr);
        else if (expr instanceof TernaryExpr)
            visitTernaryExpression((TernaryExpr) expr);
        else if (expr instanceof BitwiseComplementExpr)
            visitBitwiseComplementExpression((BitwiseComplementExpr) expr);
        else if (expr instanceof UnaryMinusExpr)
            visitUnaryMinusExpression((UnaryMinusExpr) expr);
    }

    default void visitCastExpression(CastExpr expr) {}
    default void visitStaticMethodCallExpression(StaticMethodCall expr) {}
    default void visitTrueExpression(TrueExpr expr) {}
    default void visitFalseExpression(FalseExpr expr) {}
    default void visitNumberExpression(NumExpr expr) {}
    default void visitStringExpression(StringExpr expr) {}
    default void visitIdentExpression(IdentExpr expr) {}
    default void visitNullExpression(NullExpr expr) {}
    default void visitArrayAccessExpression(ArrayAccessExpr expr) {}
    default void visitStaticFieldExpression(StaticField expr) {}
    default void visitOwnedExpression(OwnedExpr expr) {}
    default void visitNotExpression(NotExpr expr) {}
    default void visitConstructorExpression(ConstructorCallExpr expr) {}
    default void visitMethodCallExpression(MethodCallExpr expr) {}
    default void visitBinaryExpression(BinaryExpression expr) {}
    default void visitAndExpression(AndExpr expr) {}
    default void visitEqExpression(EqExpr expr) {}
    default void visitOrExpression(OrExpr expr) {}
    default void visitAssignmentExpression(AssignmentExpr expr) {}
    default void visitRangeExpression(RangeExpr expr) {}
    default void visitShiftExpression(ShiftExpr expr) {}
    default void visitAddExpression(AddExpr expr) {}
    default void visitRelExpression(RelExpr expr) {}
    default void visitBitXorExpression(BitXorExpr expr) {}
    default void visitBitOrExpression(BitOrExpr expr) {}
    default void visitMultExpression(MultExpr expr) {}
    default void visitBitAndExpression(BitAndExpr expr) {}
    default void visitArrayInitializerExpression(ArrayInitializerExpr expr) {}
    default void visitIncrementExpression(IncrementExpr expr) {}
    default void visitRescueExpression(RescueExpr expr) {}
    default void visitTernaryExpression(TernaryExpr expr) {}
    default void visitBitwiseComplementExpression(BitwiseComplementExpr expr) {}
    default void visitUnaryMinusExpression(UnaryMinusExpr expr) {}
    default void visitTerminalExpression(TerminalExpression expr) {}
}
