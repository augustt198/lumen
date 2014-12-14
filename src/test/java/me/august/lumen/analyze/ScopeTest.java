package me.august.lumen.analyze;

import me.august.lumen.compile.analyze.VariableVisitor;
import me.august.lumen.compile.analyze.var.LocalVariable;
import me.august.lumen.compile.analyze.var.Variable;
import org.junit.Assert;
import org.junit.Test;

public class ScopeTest {

    @Test
    public void testNextIndex() {
        VariableVisitor visitor = new VariableVisitor(null);

        VariableVisitor.Scope lower = visitor.scopes.push(new VariableVisitor.Scope(null));
        lower.addVariable("a", new LocalVariable(1, null));
        lower.addVariable("b", new LocalVariable(2, null));

        VariableVisitor.Scope higher = visitor.scopes.push(new VariableVisitor.Scope(null));

        Assert.assertEquals("Expected next index of 3", 3, visitor.nextLocalIndex());

        higher.addVariable("c", new LocalVariable(3, null));
        higher.addVariable("d", new LocalVariable(4, null));

        Assert.assertEquals("Expected next index of 5", 5, visitor.nextLocalIndex());
    }

    @Test
    public void testVarLookup() {
        VariableVisitor visitor = new VariableVisitor(null);

        VariableVisitor.Scope lower = visitor.scopes.push(new VariableVisitor.Scope(null));
        lower.addVariable("foo", new LocalVariable(1, null));

        VariableVisitor.Scope higher = visitor.scopes.push(new VariableVisitor.Scope(null));
        higher.addVariable("bar", new LocalVariable(2, null));

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
