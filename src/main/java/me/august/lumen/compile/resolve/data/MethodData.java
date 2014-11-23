package me.august.lumen.compile.resolve.data;

import me.august.lumen.common.Modifier;

import java.util.Arrays;

public class MethodData extends BaseData {

    private static final String CONSTRUCTOR_METHOD_NAME = "<init>";

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

    public boolean isConstructor() {
        return name.equals(CONSTRUCTOR_METHOD_NAME);
    }

    @Override
    public String toString() {
        return "MethodData{" +
            "name='" + name + '\'' +
            ", returnType='" + returnType + '\'' +
            ", paramTypes=" + Arrays.toString(paramTypes) +
            '}';
    }
}
