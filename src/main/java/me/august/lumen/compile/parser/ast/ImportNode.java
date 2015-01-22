package me.august.lumen.compile.parser.ast;

import java.util.Arrays;
import java.util.List;

public class ImportNode {

    private String path;
    private String[] classes;

    public ImportNode(String path, String... classes) {
        this.path    = path;
        this.classes = classes;
    }

    public ImportNode(String path, List<String> classes) {
        this.path    = path;
        this.classes = classes.toArray(new String[classes.size()]);
    }

    @Override
    public String toString() {
        return "ImportNode{" +
                "classes=" + Arrays.toString(classes) +
                '}';
    }

    public String getPath() {
        return path;
    }

    public String[] getClasses() {
        return classes;
    }

    public String getFull(String cls) {
        return path + '.' + cls;
    }

    public boolean hasClass(String cls) {
        for (String s : classes) {
            if (s.equals(cls)) return true;
        }

        return false;
    }
}
