package me.august.lumen.compile.resolve;

import me.august.lumen.common.Result;
import me.august.lumen.compile.parser.ast.ProgramNode;

public interface QualifiedNameResolver {

    Result<String, Exception> qualifiedName(String simpleName, ProgramNode prgm);

}
