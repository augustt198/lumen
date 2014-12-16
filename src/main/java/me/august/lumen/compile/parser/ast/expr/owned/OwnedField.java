package me.august.lumen.compile.parser.ast.expr.owned;

public class OwnedField implements OwnedExpr {

    private OwnedExpr owner;
    private String name;

    public OwnedField(String name, OwnedExpr owner) {
        this.name = name;
        this.owner = owner;
    }

    public OwnedExpr getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "OwnedField{" +
            "owner=" + owner +
            ", name='" + name + '\'' +
            '}';
    }
}
