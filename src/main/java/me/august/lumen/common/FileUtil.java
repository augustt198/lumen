package me.august.lumen.common;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static String read(String fileName) {
        return read(Paths.get(fileName));
    }

    public static String read(File file) {
        return read(file.getName());
    }

    public static String read(Path path) {
        try {
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

}
