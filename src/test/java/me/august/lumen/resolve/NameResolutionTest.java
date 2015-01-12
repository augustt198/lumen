package me.august.lumen.resolve;

import me.august.lumen.compile.parser.ast.ImportNode;
import me.august.lumen.compile.parser.ast.stmt.VarStmt;
import me.august.lumen.compile.resolve.ResolvingVisitor;
import me.august.lumen.compile.resolve.impl.NameResolver;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import org.junit.Assert;
import org.junit.Test;

public class NameResolutionTest {

    @Test
    public void testTypeResolution() {
        String qualifiedName = "foo.bar.Baz";

        ImportNode[] imports = new ImportNode[]{new ImportNode(qualifiedName)};

        NameResolver resolver = new NameResolver(imports);

        ResolvingVisitor visitor = new ResolvingVisitor(resolver);

        UnresolvedType type = new UnresolvedType("Baz");
        VarStmt var = new VarStmt("x", type);
        visitor.visitType(var);

        Assert.assertEquals(var.getResolvedType().getClassName(), qualifiedName);
    }

}
