package me.august.lumen.compile.codegen;

import me.august.lumen.compile.error.SourceException;
import me.august.lumen.compile.scanner.pos.SourcePositionProvider;
import me.august.lumen.compile.scanner.pos.Span;

import java.util.List;
import java.util.Map;

public interface BuildContext {

    int classVersion();
    List<SourceException> errors();

    void error(String msg, SourcePositionProvider src, boolean fatal);

    boolean canContinue();

    void canContinue(boolean val);

    Map<Object, Span> positionMap();
}
