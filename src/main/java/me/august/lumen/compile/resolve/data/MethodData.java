package me.august.lumen.compile.resolve.data;

import me.august.lumen.common.Modifier;

public class MethodData extends BaseData {

    private String returnType;
    private String[] paramTypes;

    public MethodData(String name, String returnType, String[] paramTypes, Modifier... modifiers) {
        super(name, modifiers);
        this.returnType = returnType;
        this.paramTypes = paramTypes;
    }

    public String getReturnType() {
        return returnType;
    }

    public String[] getParamTypes() {
        return paramTypes;
    }
}
