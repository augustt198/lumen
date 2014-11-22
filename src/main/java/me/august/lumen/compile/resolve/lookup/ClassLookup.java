package me.august.lumen.compile.resolve.lookup;

import me.august.lumen.compile.resolve.data.ClassData;

public interface ClassLookup {

    ClassData lookup(String path);
    boolean hasClass(String path);

}
