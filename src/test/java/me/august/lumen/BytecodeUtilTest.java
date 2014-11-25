package me.august.lumen;

import me.august.lumen.common.BytecodeUtil;
import org.junit.Assert;
import org.junit.Test;

public class BytecodeUtilTest {

    @Test
    public void testTypeSplit() {
        String signature = "DLjava/lang/Object;";
        String[] expected = new String[]{"D", "Ljava/lang/Object"};

        Assert.assertArrayEquals(BytecodeUtil.splitTypes(signature), expected);
    }

    @Test
    public void testPrimitiveTypeConversion() {
        Class[] types = {
            boolean.class, byte.class, char.class, short.class, int.class,
            long.class, float.class, double.class, void.class
        };
        String[] expected = {
            "Z", "B", "C", "S", "I", "J", "F", "D", "V"
        };

        for (int i = 0; i < types.length; i++) {
            Assert.assertEquals(BytecodeUtil.toType(types[i]), expected[i]);
        }
    }

    @Test
    public void testClassTypeConversion() {
        Class[] types = {
            String.class, Runnable.class, Thread.class
        };
        String[] expected = {
            "Ljava/lang/String;", "Ljava/lang/Runnable;", "Ljava/lang/Thread;"
        };

        for (int i = 0; i < types.length; i++) {
            Assert.assertEquals(BytecodeUtil.toType(types[i]), expected[i]);
        }
    }

    @Test
    public void testArrayTypeConversion() {
        Assert.assertEquals(BytecodeUtil.toType(String[].class), "[Ljava/lang/String;");

        Assert.assertEquals(BytecodeUtil.toType(long[][].class), "[[J");
    }


}
