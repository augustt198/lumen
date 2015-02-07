package me.august.lumen;

import me.august.lumen.compile.CompileBuildContext;
import me.august.lumen.compile.parser.Parser;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.scanner.Lexer;

import java.io.IOException;
import java.io.InputStream;

public class Util {

    public static String readResource(String file) {
        return readResource(file, Util.class);
    }

    public static String readResource(String file, Class<?> cls) {
        InputStream in = cls.getResourceAsStream(file);
        StringBuilder sb = new StringBuilder();

        int c;
        try {
            while ((c = in.read()) != -1) {
                sb.append((char) c);
            }
        } catch (IOException e) { return null; }

        return sb.toString();
    }

    public static ProgramNode parse(String src) {
        Lexer lexer   = new Lexer(src);
        Parser parser = new Parser(lexer, new CompileBuildContext());
        return parser.parseMain();
    }


    public static Expression parseExpression(String src) {
        Lexer lexer   = new Lexer(src);
        Parser parser = new Parser(lexer, new CompileBuildContext());

        return parser.parseExpression();
    }

}
