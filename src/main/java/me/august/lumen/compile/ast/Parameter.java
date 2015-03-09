package me.august.lumen.compile.ast;

import me.august.lumen.compile.resolve.type.BasicType;

public class Parameter extends SingleTypedNode {

    private String name;

    public Parameter(String name, BasicType type) {
        super(type);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
