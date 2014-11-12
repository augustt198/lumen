package me.august.lumen.compile;

import me.august.lumen.compile.scanner.Lexer;

import java.io.InputStream;

public class Driver {

    InputStream input;

    public Driver(InputStream input) {
        this.input = input;
    }

    public Lexer phase1Scanning() {
        return new Lexer(input);
    }
}
