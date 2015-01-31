package me.august.lumen.compile.resolve.data;

import me.august.lumen.common.ModifierSet;

public class BaseData {

    protected String name;
    protected ModifierSet modifiers;

    public BaseData(String name, ModifierSet modifiers) {
        this.name = name;
        this.modifiers = modifiers;
    }

    public String getName() {
        return name;
    }

    public ModifierSet getModifiers() {
        return modifiers;
    }

}
