package me.august.lumen.compile;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.august.lumen.common.FileUtil;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.resolve.lookup.DependencyManager;
import me.august.lumen.compile.resolve.lookup.JarLookup;
import me.august.lumen.compile.scanner.TokenSource;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.jar.JarFile;

public class CompileCommandLine implements Runnable {

    private String[] args;

    private Collection<String> keywordsToIgnore;
    private DependencyManager deps = new DependencyManager();

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

        for (String depFile : options.valuesOf(dependency)) {
            try {
                deps.addSource(new JarLookup(new JarFile(depFile)));
            } catch (IOException e) {
                throw new RuntimeException("Jar file dependency not found: " + depFile);
            }
        }

        for (Object f : options.nonOptionArguments()) {
            if (f instanceof String) {
                String file = new File((String) f).getAbsolutePath();
                String source = FileUtil.read(file);
                if (source == null) throw new RuntimeException("File not found: " + file);
                compileSource(source, file);
            }
        }
    }

    private void compileSource(String src, String file) {
        Driver driver = new Driver(src, deps);

        TokenSource lexer = driver.phase1Scanning(keywordsToIgnore);
        ProgramNode program = driver.phase2Parsing(lexer);
        driver.phase3Resolving(program);
        driver.phase4Analysis(program);

        String name = program.getClassNode().getName();
        saveBytecode(driver.phase5Bytecode(program), name, file);
    }

    private void saveBytecode(byte[] bytes, String name, String path) {
        String fileName = new File(path).getParentFile() + "/" + name + ".class";
        try {
            Files.write(Paths.get(fileName), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
