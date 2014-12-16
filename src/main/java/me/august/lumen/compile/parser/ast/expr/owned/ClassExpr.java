package me.august.lumen.compile.parser.ast.expr.owned;

/**
 * Represents a reference to a class, e.g.
 * System::out
 * ^^^^^^ the ClassExpr
 */
public class ClassExpr implements OwnedExpr {

    private String name;

    public ClassExpr(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
