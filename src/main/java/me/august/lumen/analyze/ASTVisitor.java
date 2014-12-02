package me.august.lumen.analyze;

import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.CodeBlock;
import me.august.lumen.compile.parser.ast.FieldNode;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.parser.ast.expr.MethodNode;

public abstract class ASTVisitor {

    public void visitProgram(ProgramNode program) {}
    public void visitProgramEnd(ProgramNode program) {}

    public void visitClass(ClassNode cls) {}
    public void visitClassEnd(ClassNode cls) {}

    public void visitField(FieldNode field) {}
    public void visitFieldEnd(FieldNode field) {}

    public void visitMethod(MethodNode method) {}
    public void visitMethodEnd() {}

    public void visitCodeBlock(CodeBlock code) {}
    public void visitCodeBlockEnd(CodeBlock code) {}

}
