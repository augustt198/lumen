package me.august.lumen;

import me.august.lumen.common.BytecodeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;

public class UtilTest {

    @Test
    public void testNameToOpcode() {
        Assert.assertEquals(BytecodeUtil.fromOpcodeName("NOP"), Opcodes.NOP);

        Assert.assertEquals(BytecodeUtil.fromOpcodeName("IFNONNULL"), Opcodes.IFNONNULL);
    }

    @Test
    public void testNameToAcc() {
        Assert.assertEquals(BytecodeUtil.fromAccName("ACC_PUBLIC"), Opcodes.ACC_PUBLIC);
        Assert.assertEquals(BytecodeUtil.fromAccName("ACC_DEPRECATED"), Opcodes.ACC_DEPRECATED);
    }

}
