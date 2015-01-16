package me.august.lumen;

import me.august.lumen.compile.parser.Parser;
import me.august.lumen.compile.parser.ast.expr.*;
import me.august.lumen.compile.scanner.Lexer;
import org.junit.Assert;
import org.junit.Test;

public class ExpressionTest {

    @Test
    public void testNumberExpression() {
        Expression expr = parseExpression("1");

        Assert.assertTrue(expr instanceof NumExpr);

        Assert.assertTrue(expr.equals(new NumExpr(1)));
    }

    @Test
    public void testIdentifierExpression() {
        Expression expr = parseExpression("foo");

        Assert.assertTrue(expr instanceof IdentExpr);

        Assert.assertTrue(expr.equals(new IdentExpr("foo")));
    }

    @Test
    public void testUneededParens() {
        Expression expr = parseExpression("((((((1))))))");

        Assert.assertTrue(expr instanceof NumExpr);

        Assert.assertTrue(expr.equals(new NumExpr(1)));
    }

    @Test
    public void testTernaryExpression() {
        Expression expr = parseExpression("foo ? bar : baz");

        Assert.assertTrue(expr instanceof TernaryExpr);

        TernaryExpr ternary = (TernaryExpr) expr;
        Assert.assertEquals(ternary.getCondition(), new IdentExpr("foo"));
        Assert.assertEquals(ternary.getTrueExpr(), new IdentExpr("bar"));
        Assert.assertEquals(ternary.getFalseExpr(), new IdentExpr("baz"));
    }

    @Test
    public void testStaticFieldExpression() {
        Expression expr = parseExpression("Foo::bar");

        Assert.assertTrue(expr instanceof StaticField);

        StaticField field = (StaticField) expr;
        Assert.assertEquals("Foo", field.getClassName());
        Assert.assertEquals("bar", field.getFieldName());
    }

    @Test
    public void testStaticMethodExpression() {
        Expression expr = parseExpression("Foo::bar()");

        Assert.assertTrue(expr instanceof StaticMethodCall);

        StaticMethodCall method = (StaticMethodCall) expr;
        Assert.assertEquals("Foo", method.getClassName());
        Assert.assertEquals("bar", method.getMethodName());
        Assert.assertTrue(method.getParameters().isEmpty());
    }

    @Test
    public void testOwnedExpressions() {
        Expression expr;

        expr = parseExpression("foo().bar.qux");
        Assert.assertTrue(expr instanceof IdentExpr);
        Assert.assertTrue(((OwnedExpr) expr).getTail() instanceof MethodCallExpr);

        expr = parseExpression("foo.bar.qux()");
        Assert.assertTrue(expr instanceof MethodCallExpr);
        Assert.assertTrue(((OwnedExpr) expr).getTail() instanceof IdentExpr);
    }

    @Test
    public void testCastExpression() {
        Expression expr = parseExpression("foo as Bar");

        Assert.assertTrue(expr instanceof CastExpr);
        CastExpr cast = (CastExpr) expr;

        Assert.assertTrue(cast.getValue() instanceof IdentExpr);
        Assert.assertEquals(((IdentExpr) cast.getValue()).getIdentifier(), "foo");
        Assert.assertEquals(cast.getUnresolvedType().getBaseName(), "Bar");
    }

    @Test
    public void testArrayInitializerExpr() {
        Expression expr = parseExpression("int.. [1]");

        Assert.assertTrue(expr instanceof ArrayInitializerExpr);
    }

    private Expression parseExpression(String src) {
        Lexer lexer     = new Lexer(src);
        Parser parser   = new Parser(lexer);

        return parser.parseExpression();
    }

}
