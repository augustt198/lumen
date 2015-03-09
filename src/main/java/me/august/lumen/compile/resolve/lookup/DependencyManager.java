package me.august.lumen.compile.resolve.lookup;

import me.august.lumen.compile.resolve.data.ClassData;

import java.util.ArrayList;
import java.util.List;

/**
 * DependencyManager bundles together several individual
 * class lookups to form one class lookup
 */
public class DependencyManager implements ClassLookup {

    List<ClassLookup> sources = new ArrayList<>();

    public DependencyManager() {
        sources.add(new BuiltinClassLookup());
    }

    public void addSource(ClassLookup lookup) {
        sources.add(lookup);
    }

    public List<ClassLookup> getSources() {
        return sources;
    }

    @Override
    public ClassData lookup(String path) {
        for (ClassLookup src : sources) {
            ClassData cls = src.lookup(path);
            if (cls != null) return cls;
        }
        return null;
    }

    @Override
    public boolean hasClass(String path) {
        for (ClassLookup src : sources) {
            if (src.hasClass(path)) return true;
        }
        return false;
    }
}
