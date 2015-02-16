package me.august.lumen.compile;

import me.august.lumen.common.TextualSnippet;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.scanner.pos.Span;
import org.fusesource.jansi.Ansi;

import java.util.HashMap;
import java.util.Map;

import static org.fusesource.jansi.Ansi.Attribute.*;
import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;


public class CompileBuildContext implements BuildContext {

    private static final int CLASS_VERSION = 51;

    private Map<Object, Span> positionMap = new HashMap<>();
    private boolean cont = true;

    private String source;

    public CompileBuildContext(String source) {
        this.source = source;
    }

    public CompileBuildContext() {}

    @Override
    public int classVersion() {
        return CLASS_VERSION;
    }

    @Override
    public void error(String msg, boolean fatal, Object reference) {
        if (source == null || !positionMap.containsKey(reference)) {
            Ansi ansi = ansi().a(INTENSITY_BOLD).fg(RED).a("Error: " + msg).reset();
            System.out.println(ansi);

            ansi = ansi().a(INTENSITY_BOLD).fg(RED).a("(Unknown location)").reset();
            System.out.println(ansi);
        } else {
            Span span = positionMap.get(reference);
            TextualSnippet snippet = TextualSnippet.selectLines(source, span.getStart(), span.getEnd());

            snippet.printError(msg);
        }
        System.exit(1);
    }

    @Override
    public boolean canContinue() {
        return cont;
    }

    @Override
    public void canContinue(boolean val) {
        cont = val;
    }

    @Override
    public Map<Object, Span> positionMap() {
        return positionMap;
    }

}
