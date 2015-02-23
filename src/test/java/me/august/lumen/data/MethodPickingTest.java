package me.august.lumen.data;

import junit.framework.Assert;
import me.august.lumen.common.ModifierSet;
import me.august.lumen.compile.resolve.convert.Conversions;
import me.august.lumen.compile.resolve.data.MethodData;
import me.august.lumen.compile.resolve.data.exception.AmbiguousMethodException;
import me.august.lumen.compile.resolve.lookup.BuiltinClassLookup;
import me.august.lumen.compile.resolve.lookup.ClassLookup;
import org.junit.Test;
import org.objectweb.asm.Type;

import java.io.Serializable;

public class MethodPickingTest {

    private static final ClassLookup LOOKUP = new BuiltinClassLookup();

    @Test(expected = AmbiguousMethodException.class)
    public void ambigiousMethodsTest() throws AmbiguousMethodException {
        Type[] method1Types = {Type.getType(CharSequence.class)};
        MethodData method1  = new MethodData(
                "foo", null, method1Types, new ModifierSet()
        );

        Type[] method2Types = {Type.getType(Serializable.class)};
        MethodData method2  = new MethodData(
                "foo", null, method2Types, new ModifierSet()
        );

        Type[] actualTypes      = {Type.getType(String.class)};
        MethodData[] methods    = {method1, method2};

        Conversions.pickMethod(actualTypes, methods, LOOKUP);
    }

    @Test
    public void subclassSelectionTest() throws AmbiguousMethodException {
        Type[] method1Types = {Type.getType(Throwable.class)};
        MethodData method1  = new MethodData(
                "foo", null, method1Types, new ModifierSet()
        );

        Type[] method2Types = {Type.getType(Exception.class)};
        MethodData method2  = new MethodData(
                "foo", null, method2Types, new ModifierSet()
        );

        Type[] actualTypes      = {Type.getType(RuntimeException.class)};
        MethodData[] methods    = {method1, method2};

        MethodData pick = Conversions.pickMethod(actualTypes, methods, LOOKUP);

        Assert.assertEquals(method2, pick);
    }

}
