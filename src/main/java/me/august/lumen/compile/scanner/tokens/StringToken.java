package me.august.lumen.compile.scanner.tokens;

import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.Type;

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
