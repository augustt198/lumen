package me.august.lumen;

import me.august.lumen.common.BytecodeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class TypeTest implements Opcodes {

    @Test
    public void test() {
        Type type = Type.getType(String.class);

        Assert.assertEquals("Expected ASTORE opcode", BytecodeUtil.storeInstruction(type), ASTORE);
        Assert.assertEquals("Expected ALOAD opcode", BytecodeUtil.loadInstruction(type), ALOAD);
    }

}
