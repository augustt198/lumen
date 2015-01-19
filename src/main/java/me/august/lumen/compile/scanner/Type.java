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
    RANGE,      // ..
    COLON,      // :

    SEP,        // ::

    QUESTION,   // ?

    // =========
    // operators
    // =========

    NOT,        // !

    LOGIC_OR,   // ||
    LOGIC_AND,  // &&

    BIT_OR,     // |
    BIT_XOR,    // ^
    BIT_AND,    // &
    BIT_COMP,   // ~

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
    REM,        // %

    INC,        // ++
    DEC,        // --

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

    INSTANCEOF_KEYWORD,
    NOT_INSTANCEOF_KEYWORD,

    VAR_KEYWORD,    // declare new local variable

    IF_KEYWORD,
    UNLESS_KEYWORD,
    ELSE_KEYWORD,

    THEN_KEYWORD,

    WHILE_KEYWORD,
    UNTIL_KEYWORD,

    BREAK_KEYWORD,
    NEXT_KEYWORD,

    CAST_KEYWORD,

    RETURN_KEYWORD,

    RESCUE_KEYWORD,

    NEW_KEYWORD,

    R_ARROW,

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
            case STATIC_KEYWORD:return Modifier.STATIC;
            default:            return null;
        }
    }

    public static enum Attribute {
        ACC_MOD
    }
}
