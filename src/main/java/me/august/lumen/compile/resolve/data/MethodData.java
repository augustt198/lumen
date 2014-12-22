package me.august.lumen.compile.resolve.data;

import me.august.lumen.common.Modifier;
import org.objectweb.asm.Type;

import java.util.Arrays;

public class MethodData extends BaseData {

    private static final String CONSTRUCTOR_METHOD_NAME = "<init>";

    private Type returnType;
    private Type[] paramTypes;

    public MethodData(String name, Type returnType, Type[] paramTypes, Modifier... modifiers) {
        super(name, modifiers);
        this.returnType = returnType;
        this.paramTypes = paramTypes;
    }

    public Type getReturnType() {
        return returnType;
    }

    public Type[] getParamTypes() {
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
