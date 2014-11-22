package me.august.lumen.compile.resolve.data;

import me.august.lumen.common.Modifier;

public class BaseData {

    protected String name;
    protected Modifier[] modifiers;

    public BaseData(String name, Modifier... modifiers) {
        this.name = name;
        this.modifiers = modifiers;
    }

    public String getName() {
        return name;
    }

    public Modifier[] getModifiers() {
        return modifiers;
    }
}
