package me.august.lumen;

import me.august.lumen.compile.Driver;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.scanner.Lexer;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    private static final String SRC =
        "class Foo {\n" +
            "def foo() {\n" +
                "var s: String = \"Hi\"\n"+
            "}\n" +
        "}";

    public static void main(String[] args) throws Exception {
        Reader reader = new StringReader(SRC);
        Driver driver = new Driver(reader);

        Lexer lexer         = driver.phase1Scanning();
        ProgramNode program = driver.phase2Parsing(lexer);

        driver.phase3Resolving(program);
        driver.phase4Analysis (program);

        byte[] bytecode = driver.phase5Bytecode(program);

        saveBytecode(bytecode, args[0]);
    }

    private static void saveBytecode(byte[] bytecode, String path) throws IOException {
        Files.write(Paths.get(path), bytecode);
    }

}
