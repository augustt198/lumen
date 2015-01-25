package me.august.lumen;

import me.august.lumen.compile.resolve.convert.ConversionStrategy;
import me.august.lumen.compile.resolve.convert.Conversions;
import me.august.lumen.compile.resolve.convert.types.PrimitiveWidening;
import me.august.lumen.compile.resolve.convert.types.ReferenceWidening;
import me.august.lumen.compile.resolve.convert.types.UnboxingConversion;
import me.august.lumen.compile.resolve.lookup.ClassLookup;
import me.august.lumen.compile.resolve.lookup.DependencyManager;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Type;

public class ConversionTest {

    @Test
    public void testConversions() {
        Type t1;
        Type t2;
        ConversionStrategy cs;
        ClassLookup lookup = new DependencyManager();

        t1 = Type.INT_TYPE;
        t2 = Type.DOUBLE_TYPE;
        cs = Conversions.convert(t1, t2, lookup);
        Assert.assertTrue(cs.getSteps().get(0) instanceof PrimitiveWidening);

        t1 = Type.DOUBLE_TYPE;
        t2 = Type.INT_TYPE;
        cs = Conversions.convert(t1, t2, lookup);
        Assert.assertEquals(null, cs.getSteps().get(0));

        t1 = Type.getType(Integer.class);
        t2 = Type.DOUBLE_TYPE;
        cs = Conversions.convert(t1, t2, lookup);
        Assert.assertTrue(cs.getSteps().get(0) instanceof UnboxingConversion);
        Assert.assertTrue(cs.getSteps().get(1) instanceof PrimitiveWidening);

        t1 = Type.getType(Integer.class);
        t2 = Type.getType(Number.class);
        cs = Conversions.convert(t1, t2, lookup);
        Assert.assertTrue(cs.getSteps().get(0) instanceof ReferenceWidening);
    }

}
