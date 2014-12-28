package me.august.lumen.compile.scanner.tokens;

import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.Type;

import java.util.List;

public class ImportPathToken extends Token {

    private String path;
    private List<String> classes;

    public ImportPathToken(String content, int start, int end, String path, List<String> classes) {
        super(content, start, end, Type.IMPORT_PATH);
        this.path = path;
        this.classes = classes;
    }

    public String getPath() {
        return path;
    }

    public List<String> getClasses() {
        return classes;
    }
}
