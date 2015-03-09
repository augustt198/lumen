package me.august.lumen.compile.resolve.type;

import me.august.lumen.common.BytecodeUtil;
import me.august.lumen.common.StringUtil;
import org.objectweb.asm.Type;

public class BasicType {

    public static BasicType OBJECT_TYPE =
        new BasicType("Object");

    public static BasicType VOID_TYPE =
        new BasicType("void");

    private String baseName;
    private int dims;

    public BasicType(String baseName) {
        this(baseName, 0);
    }

    public BasicType(String baseName, int dims) {
        this.baseName = baseName;
        this.dims = dims;
    }

    public boolean isArray() {
        return dims > 0;
    }

    public boolean isPrimitive() {
        return !isArray() && baseIsPrimitive();
    }

    public boolean baseIsPrimitive() {
        return BytecodeUtil.LUMEN_PRIMITIVES_SET.contains(baseName);
    }

    public String getBaseName() {
        return baseName;
    }

    public BasicType getBaseType() {
        return new BasicType(baseName);
    }

    public BasicType getComponentType() {
        if (isArray()) {
            throw new IllegalStateException("Type is not an array");
        }

        return new BasicType(baseName, dims - 1);
    }

    public int getArrayDimensions() {
        return dims;
    }

    public Type toType() {
        return BytecodeUtil.fromSimpleName(baseName, dims);
    }

    @Override
    public String toString() {
        return baseName + StringUtil.repeat("[]", dims);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasicType that = (BasicType) o;

        if (dims != that.dims) return false;
        if (baseName != null ? !baseName.equals(that.baseName) : that.baseName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = baseName != null ? baseName.hashCode() : 0;
        result = 31 * result + dims;
        return result;
    }
}
