package me.august.lumen.compile.scanner;

import me.august.lumen.common.Modifier;

public enum Type {

    L_PAREN,    // (
    R_PAREN,    // )

    L_BRACE,    // {
    R_BRACE,    // }

    L_BRACKET,  // [
    R_BRACKET,  // ]

    COMMA,      // ,
    DOT,        // .
    COLON,      // :

    SEP,        // ::

    QUESTION,   // ?

    // =========
    // operators
    // =========

    NEG,        // !

    LOGIC_OR,   // ||
    LOGIC_AND,  // &&

    BIT_OR,     // |
    BIT_XOR,    // ^
    BIT_AND,    // &

    EQ,         // ==
    NE,         // !=

    LT,         // <
    GT,         // >
    LTE,        // <=
    GTE,        // >=

    SH_L,       // <<
    SH_R,       // >>
    U_SH_R,     // >>>

    PLUS,       // +
    MIN,        // -
    MULT,       // *
    DIV,        // /

    ASSIGN,     // =

    // literals
    NUMBER,
    STRING,
    TRUE,
    FALSE,
    NULL,

    IDENTIFIER,

    IMPORT_PATH,

    // keywords
    DEF_KEYWORD,
    IMPORT_KEYWORD,
    CLASS_KEYWORD,
    STATIC_KEYWORD(Attribute.ACC_MOD),

    IS_KEYWORD,     // equivalent to Java `instanceof` keyword

    VAR_KEYWORD,    // declare new local variable

    IF_KEYWORD,
    ELSE_KEYWORD,

    WHILE_KEYWORD,

    // access modifiers
    ACC_PUBLIC(Attribute.ACC_MOD),
    ACC_PRIVATE(Attribute.ACC_MOD),
    ACC_PROTECTED(Attribute.ACC_MOD),
    ACC_PACKAGE(Attribute.ACC_MOD),

    EOF;

    private Attribute[] attrs;

    Type() {
        this(new Attribute[]{});
    }

    Type(Attribute... attrs) {
        this.attrs = attrs;
    }

    public boolean hasAttribute(Attribute a) {
        for (Attribute at : attrs) {
            if (at == a) return true;
        }
        return false;
    }

    public Modifier toModifier() {
        switch (this) {
            case ACC_PUBLIC:    return Modifier.PUBLIC;
            case ACC_PRIVATE:   return Modifier.PRIVATE;
            case ACC_PROTECTED: return Modifier.PRIVATE;
            case ACC_PACKAGE:   return Modifier.PACKAGE_PRIVATE;
            default:            return null;
        }
    }

    public static enum Attribute {
        ACC_MOD
    }
}
