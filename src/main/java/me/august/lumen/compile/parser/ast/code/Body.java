package me.august.lumen.compile.parser.ast.code;

import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.CodeBlock;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;

public class Body implements CodeBlock {

    private List<CodeBlock> children;

    public Body() {
        this(new ArrayList<>());
    }

    public Body(List<CodeBlock> children) {
        this.children = children;
    }

    private void addCode(CodeBlock code) {
        children.add(code);
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        for (CodeBlock code : children) {
            code.generate(visitor, context);
        }
    }
}
