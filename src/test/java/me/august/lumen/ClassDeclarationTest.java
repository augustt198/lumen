package me.august.lumen;

import me.august.lumen.common.Modifier;
import me.august.lumen.common.ModifierSet;
import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.ProgramNode;
import org.junit.Assert;
import org.junit.Test;

public class ClassDeclarationTest {

    static String TEST_SIMPLE_CLASS_FILE = "/test_class.lm";
    static String TEST_SUPER_CLASS_FILE  = "/test_superclass.lm";
    static String TEST_INTERFACES_FILE   = "/test_interfaces.lm";

    @Test
    public void testSimpleClass() {
        String src = Util.readResource(TEST_SIMPLE_CLASS_FILE);
        ProgramNode program = Util.parse(src);

        ClassNode cls = program.getClassNode();

        Assert.assertEquals(
                "Expected class name to be 'Foo'",
                "Foo", cls.getName()
        );

        Assert.assertEquals(
                "Expected class to have 'public' modifier",
                new ModifierSet(Modifier.PUBLIC), cls.getModifiers()
        );

        Assert.assertEquals(
                "Expected class to implement to interfaces",
                0, cls.getInterfaces().length
        );

        Assert.assertEquals(
                "Expected class to have no fields",
                0, cls.getFields().size()
        );
        Assert.assertEquals(
                "Expected class to have no methods",
                0, cls.getMethods().size()
        );
    }

    @Test
    public void testSuperClass() {
        String src = Util.readResource(TEST_SUPER_CLASS_FILE);
        ProgramNode program = Util.parse(src);

        ClassNode cls = program.getClassNode();

        String type = cls.getSuperClass().getUnresolvedType().getBaseName();
        Assert.assertEquals(
                "Expected class's superclass to be 'Bar'",
                "Bar", type
        );
    }

    @Test
    public void testInterfaces() {
        String src = Util.readResource(TEST_INTERFACES_FILE);
        ProgramNode program = Util.parse(src);

        ClassNode cls = program.getClassNode();

        String[] expectedInterfaces = new String[]{"Bar", "Qux"};

        Assert.assertArrayEquals(
                "Expected class to implement {'Bar', 'Qux'}",
                expectedInterfaces, cls.getInterfaces()
        );
    }

}
