package me.august.lumen.compile;

import me.august.lumen.compile.parser.Parser;
import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.scanner.Lexer;
import org.objectweb.asm.ClassWriter;

import java.io.Reader;

public class Driver {

    Reader reader;

    public Driver(Reader reader) {
        this.reader = reader;
    }

    public Lexer phase1Scanning() {
        return new Lexer(reader);
    }

    public Parser phase2Parsing(Lexer lexer) {
        return new Parser(lexer);
    }

    public byte[] phase3Bytecode(ClassNode cls) {
        ClassWriter writer = new ClassWriter(0);
        cls.generate(writer, () -> 49);

        return writer.toByteArray();
    }
}
