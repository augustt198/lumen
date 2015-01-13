package me.august.lumen;

import me.august.lumen.common.Modifier;
import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.ProgramNode;
import org.junit.Assert;
import org.junit.Test;

public class ClassDeclarationTest {

    static String TEST_SIMPLE_CLASS_FILE = "/test_class.lm";
    static String TEST_SUPER_CLASS_FILE = "/test_superclass.lm";
    static String TEST_INTERFACES_FILE = "/test_interfaces.lm";

    @Test
    public void testSimpleClass() {
        String src = Util.readResource(TEST_SIMPLE_CLASS_FILE);
        ProgramNode program = Util.parse(src);

        ClassNode cls = program.getClassNode();

        Assert.assertEquals(cls.getName(), "Foo");
        Assert.assertArrayEquals(cls.getModifiers(), new Modifier[]{Modifier.PUBLIC});
        Assert.assertEquals(cls.getInterfaces().length, 0);
        Assert.assertEquals(cls.getFields().size(), 0);
        Assert.assertEquals(cls.getMethods().size(), 0);
    }

    @Test
    public void testSuperClass() {
        String src = Util.readResource(TEST_SUPER_CLASS_FILE);
        ProgramNode program = Util.parse(src);

        ClassNode cls = program.getClassNode();

        String type = cls.getSuperClass().getUnresolvedType().getBaseName();
        Assert.assertEquals("Bar", type);
    }

    @Test
    public void testInterfaces() {
        String src = Util.readResource(TEST_INTERFACES_FILE);
        ProgramNode program = Util.parse(src);

        ClassNode cls = program.getClassNode();

        String[] expectedInterfaces = new String[]{"Bar", "Qux"};

        Assert.assertArrayEquals(cls.getInterfaces(), expectedInterfaces);
    }

}
