package me.august.lumen.compile.codegen;

import me.august.lumen.compile.scanner.pos.Span;

import java.util.Map;

public interface BuildContext {

    int classVersion();

    void error(String msg, boolean fatal, Object reference);

    boolean canContinue();

    void canContinue(boolean val);

    Map<Object, Span> positionMap();
}
