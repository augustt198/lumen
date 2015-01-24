package me.august.lumen.codegen;

import me.august.lumen.Util;
import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.compile.parser.ast.expr.Expression;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerationTest {

    @Test
    public void testAdditive() {
        Expression expr;
        MethodVisitorRecorder method = new MethodVisitorRecorder();

        expr   = Util.parseExpression("2 + 2");

        expr.generate(method, null);
        Assert.assertArrayEquals(
                new String[]{"ICONST_2", "ICONST_2", "IADD"},
                method.events()
        );

        expr = Util.parseExpression("100 + 2000");
        method.clear();

        expr.generate(method, null);
        Assert.assertArrayEquals(
                new String[]{"BIPUSH 100", "SIPUSH 2000", "IADD"},
                method.events()
        );

        expr = Util.parseExpression("10000000 - 20000000");
        method.clear();

        expr.generate(method, null);
        Assert.assertArrayEquals(
                new String[]{"LDC 10000000", "LDC 20000000", "ISUB"},
                method.events()
        );
    }

    @Test
    public void testMultiplicative() {
        Expression expr;
        MethodVisitorRecorder method = new MethodVisitorRecorder();

        expr = Util.parseExpression("2 * 2");

        expr.generate(method, null);
        Assert.assertArrayEquals(
                new String[]{"ICONST_2", "ICONST_2", "IMUL"},
                method.events()
        );

        expr = Util.parseExpression("100 / 200");
        method.clear();

        expr.generate(method, null);
        Assert.assertArrayEquals(
                new String[]{"BIPUSH 100", "SIPUSH 200", "IDIV"},
                method.events()
        );

        expr = Util.parseExpression("10000000 % 20000000");
        method.clear();

        expr.generate(method, null);
        Assert.assertArrayEquals(
                new String[]{"LDC 10000000", "LDC 20000000", "IREM"},
                method.events()
        );
    }

    private static class MethodVisitorRecorder extends MethodVisitor {

        private List<String> events = new ArrayList<>();

        public String[] events() {
            return events.toArray(new String[events.size()]);
        }

        public MethodVisitorRecorder() {
            super(Opcodes.ASM5);
        }

        @Override
        public void visitInsn(int i) {
            events.add(BytecodeUtil.toOpcodeName(i));
        }

        @Override
        public void visitLdcInsn(Object o) {
            events.add("LDC " + o);
        }

        @Override
        public void visitIntInsn(int i, int val) {
            String opcode = BytecodeUtil.toOpcodeName(i);
            events.add(opcode + " " + val);
        }

        public void clear() {
            events.clear();
        }
    }

}
