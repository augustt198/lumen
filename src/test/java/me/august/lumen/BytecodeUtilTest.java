package me.august.lumen;

import me.august.lumen.common.BytecodeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class BytecodeUtilTest {

    @Test
    public void testNameToOpcode() {
        Assert.assertEquals(Opcodes.NOP, BytecodeUtil.fromOpcodeName("NOP"));

        Assert.assertEquals(Opcodes.IFNONNULL, BytecodeUtil.fromOpcodeName("IFNONNULL"));
    }

    @Test
    public void testNameToAcc() {
        Assert.assertEquals(Opcodes.ACC_PUBLIC, BytecodeUtil.fromAccName("ACC_PUBLIC"));

        Assert.assertEquals(Opcodes.ACC_DEPRECATED, BytecodeUtil.fromAccName("ACC_DEPRECATED"));
    }

    @Test
    public void testNamedType() {
        Assert.assertEquals(Type.INT_TYPE, BytecodeUtil.fromSimpleName("int"));

        Assert.assertEquals(Type.BOOLEAN_TYPE, BytecodeUtil.fromSimpleName("bool"));
    }

}
