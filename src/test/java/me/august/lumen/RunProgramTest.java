package me.august.lumen;

import me.august.lumen.compile.Driver;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.scanner.TokenSource;
import org.junit.Assert;
import org.junit.Test;

import java.io.PrintStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RunProgramTest {

    private static final String SOURCE_PATH = "/program.lm";

    private static Class<?> load(final String name, final byte[] bytecode) {
        return new ClassLoader() {
            public Class load() {
                return defineClass(name, bytecode, 0, bytecode.length);
            }
        }.load();
    }

    private byte[] compile(String src) {
        Driver driver = new Driver(src);
        TokenSource lexer   = driver.phase1Scanning();

        ProgramNode pgrm = driver.phase2Parsing(lexer);
        driver.phase3Resolving(pgrm);
        driver.phase4Analysis(pgrm);

        return driver.phase5Bytecode(pgrm);
    }

    @Test
    public void testRun() throws Exception {
        String src = Util.readResource(SOURCE_PATH);

        Class<?> cls    = load("Example", compile(src));
        Method method   = cls.getMethod("run");
        Object instance = cls.newInstance();

        PrintStream old = System.out;
        PrintStreamRecorder recorder = new PrintStreamRecorder(System.out);
        System.setOut(recorder);

        method.invoke(instance);

        System.setOut(old);

        List<String> expected = new ArrayList<>();

        // println
        expected.add("Hello, world!");
        // loop
        expected.add("0"); expected.add("1"); expected.add("2");

        Assert.assertEquals(expected, recorder.lines);
    }

    private static final class PrintStreamRecorder extends PrintStream {
        private List<String> lines = new ArrayList<>();

        public PrintStreamRecorder(PrintStream ps) {
            super(ps);
        }

        @Override
        public void println(String x) {
            lines.add(x);
        }

        @Override
        public void println(int x) {
            lines.add(String.valueOf(x));
        }
    }


}
