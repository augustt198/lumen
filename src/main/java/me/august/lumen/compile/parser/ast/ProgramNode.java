package me.august.lumen.compile.parser.ast;

import java.util.Arrays;

public class ProgramNode {

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

    @Override
    public String toString() {
        return "ProgramNode{" +
            "imports=" + Arrays.toString(imports) +
            ", classNode=" + classNode +
            '}';
    }
}
