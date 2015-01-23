package me.august.lumen.analyze;

import me.august.lumen.compile.analyze.VariableVisitor;
import me.august.lumen.compile.analyze.scope.Scope;
import me.august.lumen.compile.analyze.var.LocalVariable;
import me.august.lumen.compile.analyze.var.VariableReference;
import org.junit.Assert;
import org.junit.Test;

public class ScopeTest {

    @Test
    public void testNextIndex() {
        VariableVisitor visitor = new VariableVisitor(null);

        Scope lower = new Scope(null);
        visitor.setScope(lower);

        lower.setVariable("a", new LocalVariable(1, null));
        lower.setVariable("b", new LocalVariable(2, null));

        Scope higher = new Scope(lower);
        visitor.setScope(higher);

        Assert.assertEquals(
                "Expected next index of 3",
                3, visitor.nextLocalIndex()
        );

        higher.setVariable("c", new LocalVariable(3, null));
        higher.setVariable("d", new LocalVariable(4, null));

        Assert.assertEquals(
                "Expected next index of 5",
                5, visitor.nextLocalIndex()
        );
    }

    @Test
    public void testVarLookup() {
        VariableVisitor visitor = new VariableVisitor(null);

        Scope lower = new Scope(null);
        visitor.setScope(lower);

        lower.setVariable("foo", new LocalVariable(1, null));

        Scope higher = new Scope(lower);
        visitor.setScope(higher);

        higher.setVariable("bar", new LocalVariable(2, null));

        VariableReference var = visitor.getScope().getVariable("foo");
        Assert.assertTrue(
                "Expected local variable named 'foo'",
                var instanceof LocalVariable
        );
        LocalVariable local = (LocalVariable) var;
        Assert.assertEquals(local.getIndex(), 1);

        var = visitor.getScope().getVariable("bar");
        Assert.assertTrue(
                "Expected local variable named 'bar'",
                var instanceof LocalVariable
        );
        local = (LocalVariable) var;
        Assert.assertEquals(local.getIndex(), 2);
    }

}
