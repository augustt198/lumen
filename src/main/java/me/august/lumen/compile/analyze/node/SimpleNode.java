package me.august.lumen.compile.analyze.node;

import me.august.lumen.compile.parser.ast.expr.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class SimpleNode<T> {

    private List<SimpleNode<T>> children;
    private T data;

    public SimpleNode(T data) {
        this.data = data;
        this.children = new ArrayList<>();
    }

    public SimpleNode(T data, List<SimpleNode<T>> children) {
        this.data = data;
        this.children = children;
    }

    public static <T> SimpleNode<T> createNode(Expression expr, Function<Expression, T> dataProducer) {
        SimpleNode<T> node = new SimpleNode<>(dataProducer.apply(expr));

        if (expr != null && expr.getChildren() != null && expr.getChildren().length > 0) {
            for (Expression child : expr.getChildren()) {
                node.children.add(createNode(child, dataProducer));
            }
        }

        return node;
    }

    public List<SimpleNode<T>> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public void visitTopDown(Consumer<SimpleNode<T>> visitor) {
        visitor.accept(this);
        for (SimpleNode<T> child : children) {
            child.visitTopDown(visitor);
        }
    }

    public void visitBottomDown(Consumer<SimpleNode<T>> visitor) {
        for (SimpleNode<T> child : children) {
            child.visitBottomDown(visitor);
        }
        visitor.accept(this);
    }
}
