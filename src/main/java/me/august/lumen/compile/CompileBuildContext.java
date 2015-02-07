package me.august.lumen.compile;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.error.SourceException;
import me.august.lumen.compile.scanner.pos.SourcePositionProvider;
import me.august.lumen.compile.scanner.pos.Span;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompileBuildContext implements BuildContext {

    private Map<Object, Span> positionMap = new HashMap<>();
    private List<SourceException> errors = new ArrayList<>();
    private boolean cont = true;

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

    @Override
    public Map<Object, Span> positionMap() {
        return positionMap;
    }

}
