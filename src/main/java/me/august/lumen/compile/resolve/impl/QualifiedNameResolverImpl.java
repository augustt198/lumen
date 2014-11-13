package me.august.lumen.compile.resolve.impl;

import me.august.lumen.common.Result;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.resolve.QualifiedNameResolver;

public class QualifiedNameResolverImpl implements QualifiedNameResolver {

    @Override
    public Result<String, Exception> qualifiedName(String simpleName, ProgramNode prgm) {
        try {
            return Result.ok(Class.forName("java.lang." + simpleName).getName());
        } catch (ClassNotFoundException e) {
            return Result.err(e);
        }
    }
}
