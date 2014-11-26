package me.august.lumen;

import me.august.lumen.compile.parser.Parser;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.ast.expr.IdentExpr;
import me.august.lumen.compile.parser.ast.expr.NumExpr;
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

    private Expression parseExpression(String src) {
        Lexer lexer     = new Lexer(src);
        Parser parser   = new Parser(lexer);

        return parser.parseExpression();
    }

}
