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

    public Class<? extends Number> getNumberType() {
        return value.getClass();
    }

    @Override
    public String toString() {
        return "NumberToken{" +
            "value=" + value +
            ", type=" + value.getClass().getSimpleName() +
            '}';
    }
}
