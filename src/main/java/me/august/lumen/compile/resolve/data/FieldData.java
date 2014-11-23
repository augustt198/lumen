package me.august.lumen.compile.resolve.data;

import me.august.lumen.common.Modifier;

public class FieldData extends BaseData {

    String type;

    public FieldData(String name, String type, Modifier... modifiers) {
        super(name, modifiers);
        this.type = type;
    }

    public String getType() {
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
