package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.scanner.Op;

public interface Expression {

    Expression[] getChildren();
    Op getOp();

}
