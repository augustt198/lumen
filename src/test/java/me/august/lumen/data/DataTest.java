package me.august.lumen.data;

import me.august.lumen.compile.resolve.data.ClassData;
import me.august.lumen.compile.resolve.lookup.DependencyManager;
import org.junit.Assert;
import org.junit.Test;

public class DataTest {

    @Test
    public void testClassData() {
        ClassData data = ClassData.fromClass(String.class);

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
        DependencyManager deps = new DependencyManager();
        ClassData data;

        data = ClassData.fromClass(String.class);

        Assert.assertTrue(
                "Expected String to be assignable to String",
                data.isAssignableTo("java.lang.String", deps)
        );

        Assert.assertTrue(
                "Expected String to be assignable to Object",
                data.isAssignableTo("java.lang.Object", deps)
        );

        Assert.assertTrue(
                "Expected String to be assignable to CharSequence",
                data.isAssignableTo("java.lang.CharSequence", deps)
        );

        data = ClassData.fromClass(Object.class);

        Assert.assertFalse(
                "Expected Object to not be assignable to String",
                data.isAssignableTo("java.lang.String", deps)
        );

        data = ClassData.fromClass(CharSequence.class);

        Assert.assertTrue(
                "Expected CharSequence to be assignable to Object",
                data.isAssignableTo("java.lang.Object", deps)
        );
    }

}
