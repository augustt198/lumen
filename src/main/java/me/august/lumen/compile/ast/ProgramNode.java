package me.august.lumen.compile.ast;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;

import java.util.Arrays;
import java.util.List;

public class ProgramNode implements VisitorConsumer {

    ImportNode[] imports;
    ClassNode classNode;

    public ProgramNode(ImportNode[] imports, ClassNode classNode) {
        this.imports   = imports;
        this.classNode = classNode;
    }

    public ProgramNode(List<ImportNode> imports, ClassNode classNode) {
        this.imports   = imports.toArray(new ImportNode[imports.size()]);
        this.classNode = classNode;
    }

    public ImportNode[] getImports() {
        return imports;
    }

    public void setImports(ImportNode[] imports) {
        this.imports = imports;
    }

    public ClassNode getClassNode() {
        return classNode;
    }

    public void setClassNode(ClassNode classNode) {
        this.classNode = classNode;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitProgram(this);
        classNode.accept(visitor);
        visitor.visitProgramEnd(this);
    }

    @Override
    public String toString() {
        return "ProgramNode{" +
            "imports=" + Arrays.toString(imports) +
            ", classNode=" + classNode +
            '}';
    }
}
