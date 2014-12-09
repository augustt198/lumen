package me.august.lumen.compile.parser.ast;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.resolve.impl.NameResolver;
import me.august.lumen.compile.resolve.lookup.DependencyManager;

import java.util.Arrays;

public class ProgramNode implements VisitorConsumer {

    ImportNode[] imports;
    ClassNode classNode;

    public ProgramNode(ImportNode[] imports, ClassNode classNode) {
        this.imports = imports;
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

    private void resolve() {
        resolveTypes();
    }

    private void resolveTypes() {
        NameResolver resolver = new NameResolver(imports);
        // TODO continue
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
