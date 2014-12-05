package me.august.lumen.compile.analyze;

import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.FieldNode;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.parser.ast.code.Body;
import me.august.lumen.compile.parser.ast.code.VarDeclaration;
import me.august.lumen.compile.parser.ast.expr.IdentExpr;
import me.august.lumen.compile.parser.ast.expr.MethodNode;

public abstract class ASTVisitor {

    public void visitProgram(ProgramNode program) {}
    public void visitProgramEnd(ProgramNode program) {}

    public void visitClass(ClassNode cls) {}
    public void visitClassEnd(ClassNode cls) {}

    public void visitField(FieldNode field) {}

    public void visitMethod(MethodNode method) {}
    public void visitMethodEnd(MethodNode method) {}

    public void visitBody(Body body) {}
    public void visitBodyEnd(Body body) {}

    public void visitVar(VarDeclaration var) {}

    public void visitIdentifier(IdentExpr expr) {}

}