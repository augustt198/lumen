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
        "class Thing : Hi + (Interface) {\n" +
            "field: String\n" +
            "def foo: String (bar: qux) {\n" +
                "2 * 4 / 7\n" +
            "}\n" +
        "}";

    private static final String EXPR =
        "foo(bar(1 + 3))";

    public static void main(String[] args) throws Exception {
        Reader reader = new StringReader(SRC);
        Driver driver = new Driver(reader);

        Lexer lexer         = driver.phase1Scanning();
        ProgramNode program = driver.phase2Parsing(lexer).parseMain();

        System.out.println(program);
        saveBytecode(program, args[0]);

        testExpr();
    }

    private static void testExpr() {
        Reader reader = new StringReader(EXPR);
        Driver driver = new Driver(reader);

        Lexer lexer = driver.phase1Scanning();

        Expression expr = driver.phase2Parsing(lexer).parseExpression();
        System.out.println(expr);
    }

    private static void saveBytecode(ProgramNode node, String path) throws IOException {
        BuildContext context = new Driver.CompileBuildContext();
        ClassWriter writer   = new ClassWriter(0);

        node.getClassNode().generate(writer, context);

        Files.write(Paths.get(path), writer.toByteArray());
    }

}
