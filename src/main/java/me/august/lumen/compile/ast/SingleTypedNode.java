package me.august.lumen.compile.ast;

import me.august.lumen.compile.analyze.types.BasicTypeInfo;
import me.august.lumen.compile.resolve.type.BasicType;

/**
 * Represents an AST node where a (single) type is
 * used *directly* in the associated text. "5 as Foo"
 * uses the "Foo" type directly; "5 + 5" does have
 * a type, but it does not appear directly, and
 * therefore would not be a SingleTypedNode.
 */
public class SingleTypedNode implements TypeInfoProducer {

    protected BasicTypeInfo typeInfo;

    public SingleTypedNode(BasicType type) {
        this.typeInfo = new BasicTypeInfo(type);
    }

    public BasicTypeInfo getTypeInfo() {
        return typeInfo;
    }
}
