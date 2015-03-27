package me.august.lumen.types;

import me.august.lumen.common.ModifierSet;
import me.august.lumen.compile.analyze.TypeAnalyzer;
import me.august.lumen.compile.analyze.TypeAnnotator;
import me.august.lumen.compile.analyze.VariableAnalyzer;
import me.august.lumen.compile.ast.MethodNode;
import me.august.lumen.compile.ast.expr.AddExpr;
import me.august.lumen.compile.ast.expr.AssignmentExpr;
import me.august.lumen.compile.ast.expr.IdentExpr;
import me.august.lumen.compile.ast.expr.NumExpr;
import me.august.lumen.compile.ast.stmt.Body;
import me.august.lumen.compile.ast.stmt.VarStmt;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.driver.CompileBuildContext;
import me.august.lumen.compile.resolve.impl.NameResolver;
import me.august.lumen.compile.resolve.lookup.BuiltinClassLookup;
import me.august.lumen.compile.resolve.lookup.ClassLookup;
import me.august.lumen.compile.resolve.type.BasicType;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Type;

import java.util.ArrayList;

public class TypeAnalyzerTest {

    private static final ClassLookup LOOKUP = new BuiltinClassLookup();
    private static final BuildContext BUILD = new CompileBuildContext();

    @Test
    public void testNumericTypeWidening() {
        NumExpr leftNum  = new NumExpr(1.0F); // float
        NumExpr rightNum = new NumExpr(1);    // int
        AddExpr addition = new AddExpr(leftNum, rightNum, AddExpr.Op.ADD);

        TypeAnalyzer analyzer = new TypeAnalyzer(LOOKUP, BUILD);
        addition.acceptBottomUp(analyzer);

        Assert.assertEquals(
                "Expected type of addition expression to be `float`",
                Type.FLOAT_TYPE,
                analyzer.getValue(addition)
        );
    }

    @Test
    public void testReferenceTypeWidening() {
        VarStmt var    = new VarStmt("foo", new BasicType("long"));

        AssignmentExpr assign = new AssignmentExpr(
                new IdentExpr("foo"), new NumExpr(1)
        );
        Body body = new Body(var, assign);

        MethodNode method = new MethodNode("foo", BasicType.VOID_TYPE, new ArrayList<>(), new ModifierSet());
        method.setBody(body);

        TypeAnnotator annotator = new TypeAnnotator(new NameResolver());
        method.acceptTopDown(annotator);

        VariableAnalyzer varAnalyzer = new VariableAnalyzer(annotator, BUILD);
        method.acceptTopDown(varAnalyzer);

        TypeAnalyzer typeAnalyzer = new TypeAnalyzer(LOOKUP, BUILD);
        method.acceptBottomUp(typeAnalyzer);

        Assert.assertEquals(
                "Expected assignment to be of type int",
                Type.INT_TYPE,
                typeAnalyzer.getValue(assign)
        );
    }

}
