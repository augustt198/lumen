package me.august.lumen;

import me.august.lumen.compile.Driver;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.scanner.Lexer;

import java.io.Reader;
import java.io.StringReader;

public class Main {

    private static final String SRC =
        "import something\n" +
        "class Foo : Super +(Bar, Baz) {\n" +
            "pv thing: String\n" +
        "}";

    public static void main(String[] args) throws Exception {
        Reader reader   = new StringReader(SRC);
        Driver driver   = new Driver(reader);

        Lexer lexer         = driver.phase1Scanning();
        ProgramNode program = driver.phase2Parsing(lexer);

        System.out.println(program);
    }

}
