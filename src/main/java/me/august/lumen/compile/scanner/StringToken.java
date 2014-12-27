package me.august.lumen.compile.scanner;

public class StringToken extends Token {

    private char quoteType;

    public StringToken(String content, char quoteType, int start, int end) {
        super(content, start, end, Type.STRING);
    }

    public char getQuoteType() {
        return quoteType;
    }

    @Override
    public String toString() {
        return "StringToken{" +
                "value=" + getContent() +
                ", quoteType=" + (quoteType == '"' ? "double" : "single") +
                '}';
    }

}
