package me.august.lumen.analyze;

import me.august.lumen.analyze.var.LocalVariable;
import me.august.lumen.analyze.var.Variable;
import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.ImportNode;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.parser.ast.code.Body;
import me.august.lumen.compile.parser.ast.code.VarDeclaration;
import me.august.lumen.compile.parser.ast.expr.IdentExpr;
import me.august.lumen.compile.parser.ast.expr.MethodNode;
import org.junit.Assert;
import org.junit.Test;

public class VariableResolutionTest {

    private static final ProgramNode PROGRAM;
    private static final IdentExpr IDENT_EXPR;

    /*
     * initializes data
     * PROGRAM would have this source layout:

    class Foo {
        def foo() {
            var the_var: boolean
            the_var
        }
    }
     */
    static {
        ClassNode cls = new ClassNode("Foo", "java.lang.Object", new String[0]);
        PROGRAM = new ProgramNode(new ImportNode[0], cls);

        MethodNode method = new MethodNode("foo", "void");
        Body body = new Body();
        body.addCode(new VarDeclaration("the_var", "boolean"));
        body.addCode(IDENT_EXPR = new IdentExpr("the_var"));
        method.setBody(body);

        cls.getMethods().add(method);
    }

    @Test
    public void testVariableResolution() {
        LumenVisitor visitor = new LumenVisitor(null);
        PROGRAM.accept(visitor);

        Variable var = IDENT_EXPR.getRef();
        Assert.assertTrue("Expected identifier to be local", var instanceof LocalVariable);
        Assert.assertEquals("Expected local variable to be at index 1", 1, ((LocalVariable) var).getIndex());
    }

}
