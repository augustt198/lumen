package me.august.lumen.compile;

import org.objectweb.asm.ClassWriter;
import me.august.lumen.compile.parser.Parser;
import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.ProgramNode;
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

    public ProgramNode phase2Parsing(Lexer lexer) {
        Parser parser = new Parser(lexer);
        return parser.parseMain();
    }

    public byte[] phase3Bytecode(ClassNode cls) {
        ClassWriter writer = new ClassWriter(0);
        cls.generate(writer, () -> 49);

        return writer.toByteArray();
    }
}
