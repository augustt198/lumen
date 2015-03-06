package me.august.lumen.compile.ast.stmt;

import org.objectweb.asm.Label;

public interface Loop {

    Label getRepeatLabel();
    Label getExitLabel();

}
