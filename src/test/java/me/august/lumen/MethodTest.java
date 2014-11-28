package me.august.lumen;

import static org.junit.Assert.*;

import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.parser.ast.code.IfStatement;
import me.august.lumen.compile.parser.ast.code.VarDeclaration;
import me.august.lumen.compile.parser.ast.expr.MethodNode;
import org.junit.Test;

public class MethodTest {

    private static final String TEST_METHOD_FILE = "/test_method.lm";
    private static final String TEST_STATEMENTS_FILE = "/test_statements.lm";

    @Test
    public void testMethod() {
        ProgramNode program = Util.parse(Util.readResource(TEST_METHOD_FILE));

        ClassNode cls = program.getClassNode();
        assertEquals("Expected 1 method in class", cls.getMethods().size(), 1);

        MethodNode method = cls.getMethods().get(0);
        assertEquals("Expected one code block", method.getBody().getChildren().size(), 1);

        CodeBlock code = method.getBody().getChildren().get(0);
        assertTrue("Expected code to be a variable declaration", code instanceof VarDeclaration);

        VarDeclaration var = (VarDeclaration) code;
        assertEquals("Expected variable name to be 'bar'", "bar", var.getName());
        assertEquals("Expected variable type to be 'String'", "String", var.getType());
        assertTrue("Expected variable to have default value", var.getDefaultValue() != null);
    }

    @Test
    public void testStatements() {
        ProgramNode program = Util.parse(Util.readResource(TEST_STATEMENTS_FILE));
        ClassNode cls = program.getClassNode();

        MethodNode method = cls.getMethods().get(0);
        testIfStatement(method, 0);
    }

    private void testIfStatement(MethodNode method, int idx) {
        CodeBlock code = method.getBody().getChildren().get(idx);
        assertTrue("Expected if statement", code instanceof IfStatement);
    }

}
