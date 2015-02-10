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
    TRUE("true"),
    FALSE("false"),
    NULL("null", "nil"),

    IDENTIFIER,

    IMPORT_PATH,

    // keywords
    DEF_KEYWORD("def"),
    IMPORT_KEYWORD("import"),
    CLASS_KEYWORD("class"),
    STATIC_KEYWORD(new String[]{"stc"}, Attribute.ACC_MOD),

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
    ACC_PUBLIC(new String[]{"pb", "public"},            Attribute.ACC_MOD),
    ACC_PRIVATE(new String[]{"pv", "private"},          Attribute.ACC_MOD),
    ACC_PROTECTED(new String[]{"pt", "protected"},      Attribute.ACC_MOD),
    ACC_PACKAGE(new String[]{"pk", "package_private"},  Attribute.ACC_MOD),

    EOF;

    private Attribute[] attrs;

    private String[] keywords;

    Type() {
        this(new Attribute[]{});
    }

    Type(String... keywords) {
        this.keywords = keywords;
    }

    Type(Attribute... attrs) {
        this.attrs = attrs;
    }

    Type(String[] keywords, Attribute... attrs) {
        this.keywords = keywords;
        this.attrs    = attrs;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public boolean hasAttribute(Attribute a) {
        if (attrs == null) {
            return false;
        }

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

    public boolean isModifier() {
        return toModifier() != null;
    }

    public static enum Attribute {
        ACC_MOD
    }
}
