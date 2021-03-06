package me.august.lumen.compile.parser.ast.stmt;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.VisitorConsumer;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.CodeBlock;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;

public class Body implements CodeBlock, VisitorConsumer {

    private List<CodeBlock> children;

    public Body() {
        this(new ArrayList<>());
    }

    public Body(List<CodeBlock> children) {
        this.children = children;
    }

    public Body(CodeBlock child) {
        // initialize `children`
        this();
        children.add(child);
    }

    public void addCode(CodeBlock code) {
        children.add(code);
    }

    public List<CodeBlock> getChildren() {
        return children;
    }

    public void setChildren(List<CodeBlock> children) {
        this.children = children;
    }

    @Override
    public void generate(MethodVisitor visitor, BuildContext context) {
        for (CodeBlock code : children) {
            code.generate(visitor, context);
        }
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitBody(this);
        for (CodeBlock code : children) {
            if (code instanceof VisitorConsumer)
                ((VisitorConsumer) code).accept(visitor);
        }
        visitor.visitBodyEnd(this);
    }

    @Override
    public String toString() {
        return "Body{" +
            "children=" + children +
            '}';
    }
}
