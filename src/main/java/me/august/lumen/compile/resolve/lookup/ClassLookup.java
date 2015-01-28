package me.august.lumen.compile.resolve.lookup;

import me.august.lumen.compile.resolve.data.ClassData;
import org.objectweb.asm.Type;

public interface ClassLookup {

    ClassData lookup(String path);
    boolean hasClass(String path);

    default ClassData lookup(Type type) {
        if (type.getSort() == Type.OBJECT)
            return lookup(type.getClassName());
        else
            return null;
    }

}
