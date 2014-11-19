package me.august.lumen.compile.scanner;

public enum Op {

    ADD,
    SUB,
    MUL,
    DIV,

    ASSIGN,

    LOGIC_OR,
    LOGIC_AND,

    BIT_OR,
    BIT_XOR,
    BIT_AND,

    EQ,
    NE,

    LT,
    GT,
    LTE,
    GTE,

    IS,

    SH_L,   // <<
    SH_R,   // >>
    U_SH_R, // >>>

}
