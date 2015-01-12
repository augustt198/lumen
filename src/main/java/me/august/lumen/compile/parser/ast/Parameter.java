package me.august.lumen.compile.parser.ast;

import me.august.lumen.compile.resolve.type.UnresolvedType;

public class Parameter extends Typed {

    private String name;

    public Parameter(String name, UnresolvedType type) {
        super(type);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
