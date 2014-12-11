package me.august.lumen;

import me.august.lumen.compile.CompileCommandLine;

public class Main {

    public static void main(String[] args) throws Exception {
        new CompileCommandLine(args).run();
    }

}
