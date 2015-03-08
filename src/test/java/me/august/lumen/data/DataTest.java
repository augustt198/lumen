package me.august.lumen.data;

import me.august.lumen.compile.resolve.data.ClassData;
import me.august.lumen.compile.resolve.lookup.BuiltinClassLookup;
import me.august.lumen.compile.resolve.lookup.ClassLookup;
import me.august.lumen.compile.resolve.lookup.DependencyManager;
import org.junit.Assert;
import org.junit.Test;

public class DataTest {

    @Test
    public void testClassData() {
        ClassLookup lookup = new BuiltinClassLookup();

        ClassData data = lookup.lookup(String.class);

        Assert.assertEquals(
                String.class.getName(),
                data.getName()
        );

        String[] expected = new String[]{
                "java.io.Serializable", "java.lang.Comparable",
                "java.lang.CharSequence"
        };
        Assert.assertArrayEquals(
                expected,
                data.getInterfaces()
        );
    }

    @Test
    public void testAssignableTo() {
        ClassLookup lookup = new BuiltinClassLookup();
        ClassData data;

        data = lookup.lookup(String.class);

        Assert.assertTrue(
                "Expected String to be assignable to String",
                data.isAssignableTo("java.lang.String", lookup)
        );

        Assert.assertTrue(
                "Expected String to be assignable to Object",
                data.isAssignableTo("java.lang.Object", lookup)
        );

        Assert.assertTrue(
                "Expected String to be assignable to CharSequence",
                data.isAssignableTo("java.lang.CharSequence", lookup)
        );

        data = lookup.lookup(Object.class);

        Assert.assertFalse(
                "Expected Object to not be assignable to String",
                data.isAssignableTo("java.lang.String", lookup)
        );

        data = lookup.lookup(CharSequence.class);

        Assert.assertTrue(
                "Expected CharSequence to be assignable to Object",
                data.isAssignableTo("java.lang.Object", lookup)
        );
    }

}
