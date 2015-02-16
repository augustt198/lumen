package me.august.lumen;


import me.august.lumen.compile.CompileCommandLine;
import org.fusesource.jansi.AnsiConsole;

public class Main {

    static {
        AnsiConsole.systemInstall();
    }

    public static void main(String[] args) throws Exception {
        new CompileCommandLine(args).run();
    }

}
