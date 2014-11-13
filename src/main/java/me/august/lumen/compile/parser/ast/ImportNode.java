package me.august.lumen.compile.parser.ast;

public class ImportNode {

    private String packagePath;

    // TODO multi-class import statements
    // private String[] classes;

    public ImportNode(String packagePath) {
        this.packagePath = packagePath;
    }

    @Override
    public String toString() {
        return "ImportNode{" +
            "packagePath='" + packagePath + '\'' +
            '}';
    }
}
