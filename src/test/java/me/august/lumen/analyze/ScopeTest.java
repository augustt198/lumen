package me.august.lumen.analyze;

import me.august.lumen.compile.analyze.LumenVisitor;
import me.august.lumen.compile.analyze.var.LocalVariable;
import me.august.lumen.compile.analyze.var.Variable;
import org.junit.Assert;
import org.junit.Test;

public class ScopeTest {

    @Test
    public void testNextIndex() {
        LumenVisitor visitor = new LumenVisitor(null);

        LumenVisitor.Scope lower = visitor.scopes.push(new LumenVisitor.Scope(null));
        lower.addVariable("a", new LocalVariable(1));
        lower.addVariable("b", new LocalVariable(2));

        LumenVisitor.Scope higher = visitor.scopes.push(new LumenVisitor.Scope(null));

        Assert.assertEquals("Expected next index of 3", 3, visitor.nextLocalIndex());

        higher.addVariable("c", new LocalVariable(3));
        higher.addVariable("d", new LocalVariable(4));

        Assert.assertEquals("Expected next index of 5", 5, visitor.nextLocalIndex());
    }

    @Test
    public void testVarLookup() {
        LumenVisitor visitor = new LumenVisitor(null);

        LumenVisitor.Scope lower = visitor.scopes.push(new LumenVisitor.Scope(null));
        lower.addVariable("foo", new LocalVariable(1));

        LumenVisitor.Scope higher = visitor.scopes.push(new LumenVisitor.Scope(null));
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
