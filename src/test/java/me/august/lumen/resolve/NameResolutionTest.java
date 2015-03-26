package me.august.lumen.resolve;

import me.august.lumen.compile.ast.ImportNode;
import me.august.lumen.compile.ast.stmt.VarStmt;
import me.august.lumen.compile.resolve.ResolvingVisitor;
import me.august.lumen.compile.resolve.impl.NameResolver;
import me.august.lumen.compile.resolve.type.BasicType;
import org.junit.Assert;
import org.junit.Test;

public class NameResolutionTest {

    @Test
    public void testTypeResolution() {
        String path      = "foo.bar";
        String className = "Baz";
        String qualified = path + '.' + className;

        ImportNode[] imports = new ImportNode[]{new ImportNode(path, className)};

        NameResolver resolver = new NameResolver(imports);

        ResolvingVisitor visitor = new ResolvingVisitor(resolver);

        BasicType type = new BasicType("Baz");
        VarStmt var = new VarStmt("x", type);
        visitor.visitType(var);

        Assert.assertEquals(
                qualified, var.getTypeInfo().getResolvedType().getClassName()
        );
    }

}
