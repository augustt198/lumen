package me.august.lumen.compile.parser.ast;

public class ImportNode {

    private String path;

    // TODO multi-class import statements
    // private String[] classes;

    public ImportNode(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "ImportNode{" +
            "path='" + path + '\'' +
            '}';
    }

    public String getPath() {
        return path;
    }
}
