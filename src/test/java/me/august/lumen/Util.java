package me.august.lumen;

import me.august.lumen.compile.parser.LumenParser;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.scanner.LumenScanner;
import me.august.lumen.compile.scanner.TokenSource;

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
        TokenSource lexer   = new LumenScanner(src);
        LumenParser parser = new LumenParser(lexer);
        return parser.parseProgram();
    }


    public static Expression parseExpression(String src) {
        TokenSource lexer   = new LumenScanner(src);
        LumenParser parser = new LumenParser(lexer);

        return parser.parseExpression();
    }

}
