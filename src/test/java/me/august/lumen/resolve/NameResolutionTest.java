package me.august.lumen.resolve;

import junit.framework.Assert;
import me.august.lumen.compile.parser.ast.ImportNode;
import me.august.lumen.compile.parser.ast.code.VarDeclaration;
import me.august.lumen.compile.resolve.LumenTypeVisitor;
import me.august.lumen.compile.resolve.impl.NameResolver;
import org.junit.Test;

public class NameResolutionTest {

    @Test
    public void testTypeResolution() {
        String qualifiedName = "foo.bar.Baz";

        ImportNode[] imports = new ImportNode[]{new ImportNode(qualifiedName)};

        NameResolver resolver = new NameResolver(imports);

        LumenTypeVisitor visitor = new LumenTypeVisitor(resolver, null);
        VarDeclaration var = new VarDeclaration("x", "Baz");
        visitor.visitType(var);

        Assert.assertEquals(var.getResolvedType(), qualifiedName);
    }

}
