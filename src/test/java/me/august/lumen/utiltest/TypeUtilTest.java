package me.august.lumen.utiltest;

import me.august.lumen.common.TypeUtil;
import me.august.lumen.compile.resolve.data.ClassData;
import me.august.lumen.compile.resolve.lookup.BuiltinClassLookup;
import me.august.lumen.compile.resolve.lookup.ClassLookup;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

public class TypeUtilTest {

    @Test
    public void testCommonAncestors() {
        ClassLookup lookup = new BuiltinClassLookup();

        Set<ClassData> ancestors = TypeUtil.commonAncestors(
                lookup.lookup(RuntimeException.class),
                lookup.lookup(IOException.class),
                lookup
        );

        Assert.assertEquals(1, ancestors.size());

        ClassData ancestor = ancestors.iterator().next();
        Assert.assertEquals("java.lang.Exception", ancestor.getName());
    }

}
