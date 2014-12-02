package me.august.lumen.analyze;

import static me.august.lumen.analyze.LumenVisitor.Scope;

import me.august.lumen.analyze.var.LocalVariable;
import org.junit.Assert;
import org.junit.Test;

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


}
