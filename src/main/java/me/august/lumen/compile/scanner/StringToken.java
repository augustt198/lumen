package me.august.lumen.compile.scanner;

public class StringToken extends Token {

    private QuoteType quoteType;

    public StringToken(String content, QuoteType quoteType, int start, int end) {
        super(content, start, end, Type.STRING);
    }

    public QuoteType getQuoteType() {
        return quoteType;
    }

    @Override
    public String toString() {
        return "StringToken{" +
                "value=" + getContent() +
                ", quoteType=" + quoteType.name() +
                '}';
    }

    public static enum QuoteType {

        SINGLE, DOUBLE

    }

}
