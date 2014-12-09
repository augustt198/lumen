package me.august.lumen;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class TypeTest implements Opcodes {

    @Test
    public void test() {
        Type type = Type.getType(String.class);

        Assert.assertEquals("Expected ASTORE opcode", type.getOpcode(ISTORE), ASTORE);
        Assert.assertEquals("Expected ALOAD opcode", type.getOpcode(ISTORE), ASTORE);
    }

}
