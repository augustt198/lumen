package me.august.lumen.compile;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.error.SourceException;
import me.august.lumen.compile.parser.Parser;
import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.scanner.Lexer;
import org.objectweb.asm.ClassWriter;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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

    public ProgramNode phase3Resolving(Parser parser) {
        ProgramNode program = parser.parseMain();

        return program;
    }

    public byte[] phase4Bytecode(ClassNode cls) {
        ClassWriter writer = new ClassWriter(0);
        cls.generate(writer, new CompileBuildContext());

        return writer.toByteArray();
    }

    public static class CompileBuildContext implements BuildContext {
        List<SourceException> errors = new ArrayList<>();
        boolean cont = true;
        @Override
        public int classVersion() {
            return 51;
        }

        @Override
        public List<SourceException> errors() {
            return errors;
        }

        @Override
        public boolean canContinue() {
            return cont;
        }

        @Override
        public void canContinue(boolean val) {
            cont = val;
        }
    }
}
