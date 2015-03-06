package me.august.lumen.analyze;

import me.august.lumen.common.ModifierSet;
import me.august.lumen.compile.analyze.VariableVisitor;
import me.august.lumen.compile.analyze.var.ClassVariable;
import me.august.lumen.compile.analyze.var.LocalVariable;
import me.august.lumen.compile.analyze.var.VariableReference;
import me.august.lumen.compile.ast.*;
import me.august.lumen.compile.ast.expr.IdentExpr;
import me.august.lumen.compile.ast.stmt.Body;
import me.august.lumen.compile.ast.stmt.VarStmt;
import me.august.lumen.compile.resolve.LumenTypeVisitor;
import me.august.lumen.compile.resolve.impl.NameResolver;
import me.august.lumen.compile.resolve.lookup.DependencyManager;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class VariableResolutionTest {

    private static final ProgramNode PROGRAM;
    private static final IdentExpr IDENT_EXPR;
    private static final IdentExpr FIELD_IDENT_EXPR;

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
        Typed supType = new Typed(UnresolvedType.OBJECT_TYPE);
        ClassNode cls = new ClassNode("Foo", supType, new String[0], new ModifierSet());
        PROGRAM = new ProgramNode(new ImportNode[0], cls);

        UnresolvedType type;

        type = new UnresolvedType("boolean");
        cls.getFields().add(new FieldNode("field", type, new ModifierSet()));

        type = UnresolvedType.VOID_TYPE;
        MethodNode method = new MethodNode("foo", type, new ArrayList<>(), new ModifierSet());

        Body body = new Body();

        type = new UnresolvedType("boolean");
        body.addCode(new VarStmt("the_var", type));
        body.addCode(IDENT_EXPR = new IdentExpr("the_var"));
        body.addCode(FIELD_IDENT_EXPR = new IdentExpr("field"));

        method.setBody(body);

        cls.getMethods().add(method);

        PROGRAM.accept(new LumenTypeVisitor(new NameResolver(), new DependencyManager(), null));
    }

    @Test
    public void testVariableResolution() {
        VariableVisitor visitor = new VariableVisitor(null);
        PROGRAM.accept(visitor);

        VariableReference var = IDENT_EXPR.getVariableReference();
        Assert.assertTrue(
                "Expected identifier to be local",
                var instanceof LocalVariable
        );
        Assert.assertEquals(
                "Expected local variable to be at index 1",
                1, ((LocalVariable) var).getIndex()
        );

        Assert.assertTrue(
                "Expected 'field' to be a class variable",
                FIELD_IDENT_EXPR.getVariableReference() instanceof ClassVariable
        );
    }

}
