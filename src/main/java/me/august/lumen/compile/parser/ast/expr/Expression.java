package me.august.lumen.compile.parser.ast.expr;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.codegen.MethodCodeGen;
import me.august.lumen.compile.scanner.Op;
import org.objectweb.asm.MethodVisitor;

public interface Expression extends MethodCodeGen {

    Expression[] getChildren();
    Op getOp();
    boolean isConstant();

    default void generate(MethodVisitor visitor, BuildContext context) {
        throw new UnsupportedOperationException();
    }

}
