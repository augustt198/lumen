package me.august.lumen;

import me.august.lumen.compile.Driver;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.scanner.Lexer;

import java.io.Reader;
import java.io.StringReader;

public class Main {

    private static final String SRC =
        "class Thing : Hi + (Interface) { field: String }";

    private static final String EXPR =
        "(3 / 4) + 5";

    public static void main(String[] args) throws Exception {

        Reader reader = new StringReader(SRC);
        Driver driver = new Driver(reader);

        Lexer lexer         = driver.phase1Scanning();
        ProgramNode program = driver.phase2Parsing(lexer).parseMain();

        System.out.println(program);

        testExpr();
    }

    private static void testExpr() {
        Reader reader = new StringReader(EXPR);
        Driver driver = new Driver(reader);

        Lexer lexer = driver.phase1Scanning();

        Expression expr = driver.phase2Parsing(lexer).parseExpression();
        System.out.println(expr);
    }

}
