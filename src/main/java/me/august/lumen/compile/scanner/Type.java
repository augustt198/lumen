package me.august.lumen.compile.scanner;

public enum Type {

    L_PAREN,
    R_PAREN,

    L_BRACE,
    R_BRACE,

    L_BRACKET,
    R_BRACKET,

    COMMA,

    NUMBER,
    STRING,

    IDENTIFIER,

    // keywords
    DEF_KEYWORD,

    // access modifiers
    ACC_PUBLIC,
    ACC_PRIVATE,
    ACC_PROTECTED,
    ACC_PACKAGE,

    EOF
}
