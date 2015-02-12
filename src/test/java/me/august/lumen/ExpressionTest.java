package me.august.lumen;

import me.august.lumen.compile.parser.ast.expr.*;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Type;

public class ExpressionTest {

    @Test
    public void testNumberExpression() {
        Expression expr = Util.parseExpression("1");

        Assert.assertTrue(
                "Expression should be a NumExpr",
                expr instanceof NumExpr
        );

        Assert.assertEquals(
                "Number should be 1",
                expr, new NumExpr(1)
        );
    }

    @Test
    public void testIdentifierExpression() {
        Expression expr = Util.parseExpression("foo");

        Assert.assertTrue(
                "Expression should be an IdentExpr",
                expr instanceof IdentExpr
        );

        Assert.assertEquals(
                "Identifier should be 'foo'",
                expr, new IdentExpr("foo")
        );
    }

    @Test
    public void testUneededParens() {
        Expression expr = Util.parseExpression("((((((1))))))");

        Assert.assertTrue(
                "Expected expression to be a NumExpr",
                expr instanceof NumExpr
        );

        Assert.assertEquals(
                "Number should be 1",
                expr, new NumExpr(1)
        );
    }

    @Test
    public void testTernaryExpression() {
        Expression expr = Util.parseExpression("foo ? bar : baz");

        Assert.assertTrue(expr instanceof TernaryExpr);

        TernaryExpr ternary = (TernaryExpr) expr;
        Assert.assertEquals(ternary.getCondition(), new IdentExpr("foo"));
        Assert.assertEquals(ternary.getTrueExpr(), new IdentExpr("bar"));
        Assert.assertEquals(ternary.getFalseExpr(), new IdentExpr("baz"));
    }

    @Test
    public void testStaticFieldExpression() {
        Expression expr = Util.parseExpression("Foo::bar");

        Assert.assertTrue(
                "Expression should be a StaticField",
                expr instanceof StaticField
        );

        StaticField field = (StaticField) expr;
        Assert.assertEquals("Foo", field.getClassName());
        Assert.assertEquals("bar", field.getFieldName());
    }

    @Test
    public void testStaticMethodExpression() {
        Expression expr = Util.parseExpression("Foo::bar()");

        Assert.assertTrue(
                "Expression should a StaticMethodCallExpr",
                expr instanceof StaticMethodCall
        );

        StaticMethodCall method = (StaticMethodCall) expr;
        Assert.assertEquals("Foo", method.getClassName());
        Assert.assertEquals("bar", method.getMethodName());
        Assert.assertTrue(
                "Method parameters should be empty",
                method.getParameters().isEmpty()
        );
    }

    @Test
    public void testOwnedExpressions() {
        Expression expr;
        expr = Util.parseExpression("foo().bar.qux");
        Assert.assertTrue(
                "Expression should be an IdentExpr",
                expr instanceof IdentExpr
        );
        Assert.assertTrue(
                "The identifier's tail should be a MethodCallExpr",
                ((OwnedExpr) expr).getTail() instanceof MethodCallExpr
        );

        expr = Util.parseExpression("foo.bar.qux()");
        Assert.assertTrue(
                "Expression should be a MethodCallExpr",
                expr instanceof MethodCallExpr
        );

        Assert.assertTrue(
                "The method call's tail should be an IdentExpr",
                ((OwnedExpr) expr).getTail() instanceof IdentExpr
        );
    }

    @Test
    public void testCastExpression() {
        Expression expr = Util.parseExpression("foo as Bar");

        Assert.assertTrue(
                "Expression should be a CastExpr",
                expr instanceof CastExpr
        );
        CastExpr cast = (CastExpr) expr;

        Assert.assertTrue(
                "Casting value should be an IdentExpr",
                cast.getValue() instanceof IdentExpr
        );
        Assert.assertEquals(
                "Identifier should be 'foo'",
                "foo",
                ((IdentExpr) cast.getValue()).getIdentifier()
        );

        Assert.assertEquals(
                "Cast target type should be 'Bar'",
                "Bar",
                cast.getUnresolvedType().getBaseName()
        );
    }

    @Test
    public void testArrayInitializerExpr() {
        Expression expr = Util.parseExpression("int.. [1]");

        Assert.assertTrue(
                "Expression should be an ArrayInitializerExpr",
                expr instanceof ArrayInitializerExpr
        );
    }

    @Test
    public void testArrayAccess() {
        Expression expr = Util.parseExpression("foo[0]");

        Assert.assertTrue(
                "Expression should be an ArrayAccessExpr",
                expr instanceof ArrayAccessExpr
        );
    }

    @Test
    public void testPrecedence() {
        Expression expr = Util.parseExpression("1 * 2 + 3 * 4");

        Assert.assertTrue(
                "Top level expression should be addition",
                expr instanceof AddExpr
        );
    }

    @Test
    public void testRescue() {
        Expression expr;
        RescueExpr rescue;

        expr = Util.parseExpression("2 / 0 rescue 10");

        Assert.assertTrue(
                "Expression should be a RescueExpr",
                expr instanceof RescueExpr
        );

        rescue = (RescueExpr) expr;

        Assert.assertEquals("Expected `Exception` type", Type.getType(Exception.class), rescue.getResolvedType());


        expr = Util.parseExpression("2 / 0 rescue ArithmeticException -> 10");

        Assert.assertTrue(
                "Expression should be a RescueExpr",
                expr instanceof RescueExpr
        );

        rescue = (RescueExpr) expr;

        UnresolvedType expected = new UnresolvedType("ArithmeticException");
        Assert.assertEquals(
                "Expected `ArithmeticException` type",
                expected,
                rescue.getUnresolvedType()
        );
    }


}
