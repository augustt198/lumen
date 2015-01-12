package me.august.lumen.compile.resolve.type;

import me.august.lumen.common.BytecodeUtil;
import org.objectweb.asm.Type;

public class UnresolvedType {

    public static UnresolvedType OBJECT_TYPE =
        new UnresolvedType("Object");

    public static UnresolvedType VOID_TYPE =
        new UnresolvedType("void");

    private String baseName;
    private int dims;

    public UnresolvedType(String baseName) {
        this(baseName, 0);
    }

    public UnresolvedType(String baseName, int dims) {
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

    public int getArrayDimensions() {
        return dims;
    }

    public Type toType() {
        return BytecodeUtil.fromSimpleName(baseName, dims);
    }

}
