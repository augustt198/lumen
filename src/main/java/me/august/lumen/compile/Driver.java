package me.august.lumen.compile;

import me.august.lumen.compile.analyze.VariableVisitor;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.error.SourceException;
import me.august.lumen.compile.scanner.pos.SourcePositionProvider;
import me.august.lumen.compile.parser.Parser;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.resolve.LumenTypeVisitor;
import me.august.lumen.compile.resolve.ResolvingVisitor;
import me.august.lumen.compile.resolve.TopLevelStatementMarker;
import me.august.lumen.compile.resolve.impl.NameResolver;
import me.august.lumen.compile.resolve.lookup.DependencyManager;
import me.august.lumen.compile.scanner.Lexer;
import org.objectweb.asm.ClassWriter;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Driver {

    Reader reader;
    private CompileBuildContext context = new CompileBuildContext();

    private NameResolver resolver;
    private DependencyManager deps;

    public Driver(Reader reader, DependencyManager deps) {
        this.reader = reader;
        this.deps = deps;
    }

    public Driver(Reader reader) {
        this(reader, new DependencyManager());
    }

    public Lexer phase1Scanning() {
        return new Lexer(reader);
    }

    public Lexer phase1Scanning(Collection<String> ignore) {
        Lexer lex = phase1Scanning();
        ignore.forEach(lex::ignore);
        return lex;
    }

    public ProgramNode phase2Parsing(Lexer lexer) {
        return new Parser(lexer).parseMain();
    }

    public ProgramNode phase3Resolving(ProgramNode program) {
        resolver = new NameResolver(program.getImports());
        ResolvingVisitor typeVisitor = new ResolvingVisitor(resolver);
        program.accept(typeVisitor);

        return program;
    }

    public ProgramNode phase4Analysis(ProgramNode program) {
        VariableVisitor visitor = new VariableVisitor(context);
        program.accept(visitor);

        LumenTypeVisitor typeVisitor = new LumenTypeVisitor(resolver, deps, context);
        program.accept(typeVisitor);

        program.accept(new TopLevelStatementMarker());

        return program;
    }

    public byte[] phase5Bytecode(ProgramNode program) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        program.getClassNode().generate(writer, context);

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
        public void error(String msg, SourcePositionProvider src) {
            SourceException ex = new SourceException(msg);
            ex.beginPos(src.getStart()).endPos(src.getEnd());

            errors.add(ex);
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
