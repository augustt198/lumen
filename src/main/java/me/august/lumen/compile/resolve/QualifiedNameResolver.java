package me.august.lumen.compile.resolve;

import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.resolve.data.ClassData;

public interface QualifiedNameResolver {

    ClassData fromSimpleName(String simpleName, ProgramNode prgm);

}
