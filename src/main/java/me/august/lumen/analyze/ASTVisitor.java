package me.august.lumen.analyze;

import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.parser.ast.FieldNode;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.parser.ast.expr.MethodNode;

public interface ASTVisitor {

    void visitProgram(ProgramNode program);
    default void visitProgramEnd(ProgramNode program) {}

    void visitClass(ClassNode cls);
    default void visitClassEnd(ClassNode cls) {};

    void visitField(FieldNode field);
    default void visitFieldEnd(FieldNode field) {}

    void visitMethod(MethodNode method);
    default void visitMethodEnd() {}

    void visitCodeBlock(CodeBlock code);
    default void visitCodeBlockEnd(CodeBlock code) {}

}
