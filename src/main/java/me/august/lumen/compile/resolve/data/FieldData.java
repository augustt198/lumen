package me.august.lumen.compile.resolve.data;

import me.august.lumen.common.ModifierSet;
import org.objectweb.asm.Type;

public class FieldData extends BaseData {

    private Type type;

    public FieldData(String name, Type type, ModifierSet modifiers) {
        super(name, modifiers);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "FieldData{" +
            "name='" + name + '\'' +
            ", type='" + type + '\'' +
            '}';
    }
}
