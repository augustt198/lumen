package me.august.lumen.compile;

import me.august.lumen.compile.analyze.VariableVisitor;
import me.august.lumen.compile.parser.LumenParser;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.resolve.LumenTypeVisitor;
import me.august.lumen.compile.resolve.ResolvingVisitor;
import me.august.lumen.compile.resolve.TopLevelStatementMarker;
import me.august.lumen.compile.resolve.impl.NameResolver;
import me.august.lumen.compile.resolve.lookup.DependencyManager;
import me.august.lumen.compile.scanner.LumenScanner;
import me.august.lumen.compile.scanner.TokenSource;
import org.objectweb.asm.ClassWriter;

import java.io.Reader;
import java.util.Collection;

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

    public TokenSource phase1Scanning() {
        return new LumenScanner(reader);
    }

    public TokenSource phase1Scanning(Collection<String> ignore) {
        TokenSource lexer = phase1Scanning();

        // TODO fix
        // ignore.forEach(lex::ignore);

        return lexer;
    }

    public ProgramNode phase2Parsing(TokenSource lexer) {
        return new LumenParser(lexer).parseProgram();
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

}
