package me.august.lumen.compile;

import me.august.lumen.common.FileUtil;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.scanner.Lexer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CompileCommandLine implements Runnable {

    private String[] args;

    public CompileCommandLine(String[] args) {
        this.args = args;
    }

    @Override
    public void run() {
        for (String file : args) {
            String source = FileUtil.read(file);
            if (source == null) throw new RuntimeException("File not found: " + file);
            compileSource(source, file);
        }
    }

    private void compileSource(String src, String file) {
        Driver driver = new Driver(new StringReader(src));

        Lexer lexer = driver.phase1Scanning();
        ProgramNode program = driver.phase2Parsing(lexer);
        driver.phase3Resolving(program);
        driver.phase4Analysis(program);

        String name = program.getClassNode().getName();
        saveBytecode(driver.phase5Bytecode(program), name, file);
    }

    private void saveBytecode(byte[] bytes, String name, String path) {
        String fileName = new File(path).getParentFile().getName() + "/" + name + ".class";
        try {
            Files.write(Paths.get(fileName), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
