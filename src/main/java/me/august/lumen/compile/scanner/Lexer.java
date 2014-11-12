package me.august.lumen.compile.scanner;

import static me.august.lumen.compile.scanner.Type.*;

import me.august.lumen.common.Chars;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Lexer implements Iterable<Token> {

    private static final Map<String, Type> KEYWORDS = new HashMap<>();

    static {
        Object[][] pairs = {
            {"def",     DEF_KEYWORD},

            {"pb",      ACC_PUBLIC},
            {"public",  ACC_PUBLIC},
            {"pv",      ACC_PRIVATE},
            {"private", ACC_PRIVATE},
            {"pt",      ACC_PROTECTED},
            {"protected",       ACC_PROTECTED},
            {"pk",              ACC_PACKAGE},
            {"package_private", ACC_PACKAGE}
        };
        for (Object[] pair : pairs) {
            KEYWORDS.put((String) pair[0], (Type) pair[1]);
        }
    }

    private Reader reader;
    private int pos;

    public Lexer(Reader reader) {
        this.reader = reader;
    }

    public Lexer(String src) {
        this(new StringReader(src));
    }

    public Lexer(InputStream in) {
        this(new InputStreamReader(in));
    }

    // reading methods
    private int read() {
        try {
            pos++;
            return reader.read();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read character: " + e.getMessage(), e);
        }
    }

    private int peek() {
        try {
            reader.mark(1);
            int peek = reader.read();
            reader.reset();
            return peek;
        } catch (IOException e) {
            throw new RuntimeException("Failed to peek character: " + e.getMessage(), e);
        }
    }

    public Token nextToken() {
        while (true) {
            int read = read();
            if (read == -1) return token(EOF);
            char c = (char) read;

            if      (c == '(') return token(L_PAREN);
            else if (c == ')') return token(R_PAREN);
            else if (c == '{') return token(L_BRACE);
            else if (c == '}') return token(R_BRACE);
            else if (c == '[') return token(L_BRACKET);
            else if (c == ']') return token(R_BRACKET);

            else if (c == ':') return token(COMMA);

            else if (Chars.isAlpha(c)) return nextIdent(c);
            else if (Chars.isDigit(c)) return nextNumber(c);
            else if (c == '"') return nextString();

            else if (c == ' ' || c == '\r' || c == '\n' || c =='\t') {
                continue;
            } else {
                throw new RuntimeException("Unexpected character: " + c);
            }
        }
    }

    private Token token(Type type) {
        return new Token(null, pos, pos + 1, type);
    }

    private Token nextIdent(char firstChar) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstChar);

        int startPos = pos;
        int endPos   = pos;

        while (true) {
            int peek = peek();
            if (peek == -1) break;
            char c = (char) peek;

            if (Chars.isAlpha(c) || Chars.isDigit(c)) {
                sb.append(c);
                read(); // consume character
                endPos++;
            } else {
                break;
            }
        }
        String ident = sb.toString();

        Token token = new Token(ident, startPos, endPos, null);
        Type type = KEYWORDS.get(ident);
        if (type == null) type = IDENTIFIER;
        token.setType(type);

        return token;
    }

    private Token nextNumber(char firstDigit) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstDigit);

        int startPos = pos;
        int endPos   = pos;

        while (true) {
            int peek = peek();
            if (peek == -1) break;
            char c = (char) peek;

            if (Chars.isDigit(c) || c == '.') {
                sb.append(c);
                read();
                endPos++;
            } else {
                break;
            }
        }

        return new Token(sb.toString(), startPos, endPos, NUMBER);
    }

    private Token nextString() {
        StringBuilder sb = new StringBuilder();

        int startPos = pos;
        int endPos   = pos;

        while (true) {
            int read = read();
            if (read == -1) throw new RuntimeException("Unexpected EOF in String literal");

            if (read == '"') {
                break;
            } else {
                sb.append((char) read);
                endPos++;
            }
        }

        return new Token(sb.toString(), startPos, endPos, STRING);
    }

    @Override
    public Iterator<Token> iterator() {
        return new Iterator<Token>() {
            boolean done = false;
            @Override
            public boolean hasNext() {
                return !done;
            }

            @Override
            public Token next() {
                Token next = nextToken();
                if (next.getType() == EOF) done = true;
                return next;
            }
        };
    }
}
