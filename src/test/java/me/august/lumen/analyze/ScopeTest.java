package me.august.lumen.analyze;

import me.august.lumen.analyze.var.LocalVariable;
import me.august.lumen.analyze.var.Variable;
import org.junit.Assert;
import org.junit.Test;

import static me.august.lumen.analyze.LumenVisitor.Scope;

public class ScopeTest {

    @Test
    public void testNextIndex() {
        LumenVisitor visitor = new LumenVisitor(null);

        Scope lower = visitor.scopes.push(new Scope(null));
        lower.addVariable("a", new LocalVariable(1));
        lower.addVariable("b", new LocalVariable(2));

        Scope higher = visitor.scopes.push(new Scope(null));

        Assert.assertEquals("Expected next index of 3", 3, visitor.nextLocalIndex());

        higher.addVariable("c", new LocalVariable(3));
        higher.addVariable("d", new LocalVariable(4));

        Assert.assertEquals("Expected next index of 5", 5, visitor.nextLocalIndex());
    }

    @Test
    public void testVarLookup() {
        LumenVisitor visitor = new LumenVisitor(null);

        Scope lower = visitor.scopes.push(new Scope(null));
        lower.addVariable("foo", new LocalVariable(1));

        Scope higher = visitor.scopes.push(new Scope(null));
        higher.addVariable("bar", new LocalVariable(2));

        Variable var = visitor.getVariable("foo");
        Assert.assertTrue("Expected local variable named 'foo'", var instanceof LocalVariable);
        LocalVariable local = (LocalVariable) var;
        Assert.assertEquals(local.getIndex(), 1);

        var = visitor.getVariable("bar");
        Assert.assertTrue("Expected local variable named 'bar'", var instanceof LocalVariable);
        local = (LocalVariable) var;
        Assert.assertEquals(local.getIndex(), 2);
    }

}
