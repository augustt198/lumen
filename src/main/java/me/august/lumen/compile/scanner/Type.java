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

    RANGE_EXCLUSIVE, // ..
    RANGE_INCLUSIVE, // ...

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
    DEF_KEYWORD("def"),
    IMPORT_KEYWORD("import"),
    CLASS_KEYWORD("class"),
    STATIC_KEYWORD("stc", Attribute.ACC_MOD),

    INSTANCEOF_KEYWORD,
    NOT_INSTANCEOF_KEYWORD,

    VAR_KEYWORD("var"),    // declare new local variable

    IF_KEYWORD("if"),
    UNLESS_KEYWORD("unless"),
    ELSE_KEYWORD("else"),

    THEN_KEYWORD("then"),
    DO_KEYWORD("do"),

    WHILE_KEYWORD("while"),
    UNTIL_KEYWORD("until"),


    FOR_KEYWORD("for"),
    EACH_KEYWORD("each"),

    // IN_KEYWORD, disabled (clashes with System::in)

    BREAK_KEYWORD("break"),
    NEXT_KEYWORD("next"),

    CAST_KEYWORD("as"),

    RETURN_KEYWORD("return"),

    RESCUE_KEYWORD("rescue"),

    NEW_KEYWORD("new"),

    R_ARROW,

    // access modifiers
    ACC_PUBLIC("pb", Attribute.ACC_MOD),
    ACC_PRIVATE("pv", Attribute.ACC_MOD),
    ACC_PROTECTED("pt", Attribute.ACC_MOD),
    ACC_PACKAGE("pk", Attribute.ACC_MOD),

    EOF;

    private Attribute[] attrs;

    private String keyword;

    Type() {
        this(new Attribute[]{});
    }

    Type(String keyword) {
        this.keyword = keyword;
    }

    Type(Attribute... attrs) {
        this.attrs = attrs;
    }

    Type(String keyword, Attribute... attrs) {
        this.keyword = keyword;
        this.attrs   = attrs;
    }

    public String getKeyword() {
        return keyword;
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
