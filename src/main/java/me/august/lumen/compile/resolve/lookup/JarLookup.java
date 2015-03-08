package me.august.lumen.compile.resolve.lookup;

import me.august.lumen.compile.resolve.data.ClassData;
import me.august.lumen.compile.resolve.lookup.factory.ASMClassDataFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarLookup implements ClassLookup {

    private JarFile jar;
    private Map<String, ClassData> cache = new HashMap<>();

    public JarLookup(JarFile jar) {
        this.jar = jar;
    }

    @Override
    public ClassData lookup(String path) {
        path = path.replace('.', '/');

        // Jar entries end with .class
        if (!path.endsWith(".class")) path += ".class";

        ClassData cached = cache.get(path);
        if (cached != null) return cached;

        JarEntry entry = jar.getJarEntry(path);

        try {
            ClassData classData = ASMClassDataFactory.createClassData(
                    jar.getInputStream(entry)
            );

            cache.put(path, classData);
            return classData;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean hasClass(String path) {
        path = checkName(path);

        if (cache.containsKey(path)) return true;

        return jar.getEntry(path) != null;
    }

    private String checkName(String pathName) {
        if (!pathName.endsWith(".class")) pathName += ".class";
        return pathName;
    }
}
