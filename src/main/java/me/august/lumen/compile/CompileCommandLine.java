package me.august.lumen.compile;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.august.lumen.common.FileUtil;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.scanner.Lexer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class CompileCommandLine implements Runnable {

    private String[] args;

    private Collection<String> keywordsToIgnore;
    private Collection<String> dependencies;

    public CompileCommandLine(String[] args) {
        this.args = args;
    }

    @Override
    public void run() {
        OptionParser parser = new OptionParser();
        OptionSpec<String> ignore = parser.accepts("ignore").withRequiredArg().describedAs("Keywords for the lexer to ignore");
        OptionSpec<String> dependency = parser.accepts("dependency").withRequiredArg().describedAs("Jar files to use as dependencies");

        OptionSet options = parser.parse(args);
        keywordsToIgnore = options.valuesOf(ignore);
        dependencies = options.valuesOf(dependency);

        for (Object f : options.nonOptionArguments()) {
            if (f instanceof String) {
                String file = (String) f;
                String source = FileUtil.read(file);
                if (source == null) throw new RuntimeException("File not found: " + file);
                compileSource(source, file);
            }
        }
    }

    private void compileSource(String src, String file) {
        Driver driver = new Driver(new StringReader(src));

        Lexer lexer = driver.phase1Scanning(keywordsToIgnore);
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
