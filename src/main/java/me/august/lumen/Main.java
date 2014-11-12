package me.august.lumen;

import me.august.lumen.compile.scanner.Lexer;
import me.august.lumen.compile.scanner.Token;

public class Main {

    public static void main(String[] args) {
        String src = "def hello(world) { }";
        for (Token t : new Lexer(src)) {
            System.out.println(t);
        }
    }

}
