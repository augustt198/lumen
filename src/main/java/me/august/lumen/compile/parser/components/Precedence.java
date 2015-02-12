package me.august.lumen.compile.parser.components;

public enum Precedence {

    // IMPORTANT: Precedence level of enum constant is
    // determined by its textual order (ordinal value)

    ASSIGNMENT,
    RESCUE,
    TERNARY,
    RANGE,
    LOGIC_OR,
    LOGIC_AND,
    BIT_OR,
    BIT_XOR,
    BIT_AND,
    EQUALITY,
    RELATIONAL,
    SHIFT,
    ADDITIVE,
    MULTIPLICATIVE,
    CAST,
    PREFIX,
    POSTFIX,
    CALL;

    public int getLevel() {
        return ordinal() + 1;
    }

}
