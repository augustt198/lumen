package me.august.lumen;

import me.august.lumen.compile.parser.Parser;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.scanner.Lexer;

public class Main {

    public static void main(String[] args) {
        // TODO fix superclass & interface parsing conflict
        Lexer lexer = new Lexer("import something\npv class Foo +(Bar, Baz)");
        ProgramNode prgm = new Parser(lexer).parseMain();

        System.out.println(prgm);
    }

}
