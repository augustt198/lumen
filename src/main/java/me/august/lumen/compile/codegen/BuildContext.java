package me.august.lumen.compile.codegen;

import me.august.lumen.compile.error.SourceException;

import java.util.List;

public interface BuildContext {

    int classVersion();
    List<SourceException> errors();

    boolean canContinue();

    void canContinue(boolean val);
}
