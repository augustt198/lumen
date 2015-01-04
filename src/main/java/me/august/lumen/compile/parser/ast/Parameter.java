package me.august.lumen.compile.parser.ast;

public class Parameter extends Typed {

    private String name;

    public Parameter(String name, String type) {
        super(type);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
