package me.august.lumen;

import me.august.lumen.compile.ast.ClassNode;
import me.august.lumen.compile.ast.CodeBlock;
import me.august.lumen.compile.ast.MethodNode;
import me.august.lumen.compile.ast.ProgramNode;
import me.august.lumen.compile.ast.stmt.IfStmt;
import me.august.lumen.compile.ast.stmt.VarStmt;
import me.august.lumen.compile.ast.stmt.WhileStmt;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MethodTest {

    private static final String TEST_METHOD_FILE     = "/test_method.lm";
    private static final String TEST_STATEMENTS_FILE = "/test_statements.lm";

    @Test
    public void testMethod() {
        ProgramNode program = Util.parse(Util.readResource(TEST_METHOD_FILE));

        ClassNode cls = program.getClassNode();
        assertEquals(
                "Expected 1 method in class",
                1, cls.getMethods().size()
        );

        MethodNode method = cls.getMethods().get(0);
        assertEquals(
                "Expected one code block",
                1, method.getBody().getChildren().size()
        );

        CodeBlock code = method.getBody().getChildren().get(0);
        assertTrue(
                "Expected code to be a variable declaration",
                code instanceof VarStmt
        );

        VarStmt var = (VarStmt) code;
        assertEquals(
                "Expected variable name to be 'bar'",
                "bar", var.getName()
        );
        assertEquals(
                "Expected variable type to be 'String'",
                "String", var.getUnresolvedType().getBaseName()
        );
        assertTrue(
                "Expected variable to have default value",
                var.getDefaultValue() != null
        );
    }

    @Test
    public void testStatements() {
        ProgramNode program = Util.parse(Util.readResource(TEST_STATEMENTS_FILE));
        ClassNode cls = program.getClassNode();

        MethodNode method = cls.getMethods().get(0);

        testIfStatement(method, 0);
        testWhileStatement(method, 1);
    }

    private void testIfStatement(MethodNode method, int idx) {
        CodeBlock code = method.getBody().getChildren().get(idx);
        assertTrue(
                "Expected if statement",
                code instanceof IfStmt
        );
    }

    private void testWhileStatement(MethodNode method, int idx) {
        CodeBlock code = method.getBody().getChildren().get(idx);
        assertTrue(
                "Expected while statement",
                code instanceof WhileStmt
        );
    }

}
