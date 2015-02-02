package me.august.lumen.compile.codegen;

import me.august.lumen.compile.error.SourceException;
import me.august.lumen.compile.scanner.pos.SourcePositionProvider;

import java.util.List;

public interface BuildContext {

    int classVersion();
    List<SourceException> errors();
    void error(String msg, SourcePositionProvider src);

    boolean canContinue();

    void canContinue(boolean val);
}
