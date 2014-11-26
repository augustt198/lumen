package me.august.lumen;

import me.august.lumen.common.Modifier;
import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.ProgramNode;
import org.junit.Assert;
import org.junit.Test;

public class MethodParseTest {

    static String TEST_SIMPLE_CLASS_FILE = "/test_class.lm";

    @Test
    public void testSimpleClass() {
        String src = Util.readResource(TEST_SIMPLE_CLASS_FILE);
        ProgramNode program = Util.parse(src);

        System.out.println(program);

        ClassNode cls = program.getClassNode();

        Assert.assertEquals(cls.getName(), "Foo");
        Assert.assertArrayEquals(cls.getModifiers(), new Modifier[]{Modifier.PUBLIC});
        Assert.assertEquals(cls.getInterfaces().length, 0);
        Assert.assertEquals(cls.getFields().size(), 0);
        Assert.assertEquals(cls.getMethods().size(), 0);
    }

}
