package me.august.lumen.compile.parser.ast;

import me.august.lumen.common.Modifier;

import java.util.Arrays;

public class ClassNode {

    private Modifier[] modifiers;
    private String name;
    private String superClass;
    private String[] interfaces;

    public ClassNode(String name, String superClass, String[] interfaces, Modifier... modifiers) {
        this.name       = name;
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.modifiers  = modifiers;
    }

    @Override
    public String toString() {
        return "ClassNode{" +
            "modifiers=" + Arrays.toString(modifiers) +
            ", name='" + name + '\'' +
            ", superClass='" + superClass + '\'' +
            ", interfaces=" + Arrays.toString(interfaces) +
            '}';
    }
}
