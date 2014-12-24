package me.august.lumen.compile.scanner;

public class NumberToken extends Token {

    private Number value;

    public NumberToken(Number value, int start, int end) {
        super(value.toString(), start, end, Type.NUMBER);
        this.value = value;
    }

    public Number getValue() {
        return value;
    }
}
