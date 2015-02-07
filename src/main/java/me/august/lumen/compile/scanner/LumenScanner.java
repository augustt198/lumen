package me.august.lumen.compile.scanner;

import me.august.lumen.common.Chars;
import me.august.lumen.compile.scanner.tokens.NumberToken;
import me.august.lumen.compile.scanner.tokens.StringToken;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static me.august.lumen.compile.scanner.Type.*;

public class LumenScanner implements TokenSource {

    private static Map<String, Type> KEYWORD_MAP = new HashMap<>();

    // initialize keyword map
    static {
        for (Type type : Type.values()) {
            if (type.getKeyword() != null) {
                KEYWORD_MAP.put(type.getKeyword(), type);
            }
        }
    }

    private static Map<Type, Runnable> KEYWORD_HANDLERS = new HashMap<>();

    // initialize keyword handlers
    static {
        KEYWORD_HANDLERS.put(Type.IMPORT_PATH, () -> {});
    }

    private Reader reader;

    private int currentPosition;
    private int lastRecordedPosition;

    // in case we need to insert more tokens
    // before the next token is read
    private Stack<Token> queuedTokens = new Stack<>();

    public LumenScanner(Reader reader) {
        this.reader = reader;
    }

    public LumenScanner(String source) {
        this.reader = new StringReader(source);
    }

    // =========================
    // Local utility methods
    // =========================
    private int read() {
        try {
            currentPosition++;
            return reader.read();
        } catch (IOException e) {
            // revert previous increment if
            // reading fails
            currentPosition--;
            return -1;
        }
    }

    private int peek() {
        try {
            reader.mark(1);
            int chr = reader.read();
            reader.reset();

            return chr;
        } catch (IOException e) {
            return -1;
        }
    }

    private boolean accept(int chr) {
        if (peek() == chr) {
            read();
            return true;
        } else {
            return false;
        }
    }

    private int advanceRecorder() {
        int recorded = lastRecordedPosition;
        lastRecordedPosition = currentPosition;

        return recorded;
    }

    private void consumeWhiteSpace() {
        while (peek() == ' ') read();
    }

    private Token newToken(Type type) {
        return newToken(type, null);
    }

    private Token newToken(Type type, String source) {
        return new Token(source, advanceRecorder(), currentPosition, type);
    }

    @Override
    public Token nextToken() {
        while (true) {
            int chr = read();

            switch (chr) {
                // End of file (or error) reached
                case -1: return newToken(EOF);

                // Bracket tokens
                case '(': return newToken(L_PAREN);
                case ')': return newToken(R_PAREN);
                case '{': return newToken(L_BRACKET);
                case '}': return newToken(R_BRACE);
                case '[': return newToken(L_BRACKET);
                case ']': return newToken(R_BRACKET);

                case '"':
                case '\'': return nextStringLiteral((char) chr);

                // arithmetic operators
                case '+': return diffPlus();
                case '-': return diffMin();
                case '*': return newToken(MULT);
                case '/': return newToken(DIV);
                case '%': return newToken(REM);

                // punctuation
                case ',': return newToken(COMMA);
                case '.': return diffDots();
                case ':': return diffColon();

                case '>': return diffGT();
                case '<': return diffLT();

                case '|': return diffOr();
                case '&': return diffAnd();
                case '~': return newToken(BIT_COMP);

                case '!': return diffBang();
                case '?': return newToken(QUESTION);

                case '=': return diffEq();

                case ' ' :
                case '\n':
                case '\r':
                case '\t': continue;

                default: {
                    char c = (char) chr;

                    if (Chars.isIdentifierStart(c)) {
                        return nextIdentifier(c);
                    } else if (Chars.isDigit(c)) {
                        return nextNumber(c);
                    }
                }
            }

        }
    }

    /**
     * Reads the next string literal
     * @param startQuote The starting quote type (" or ')
     * @return A StringToken with the string literal's content
     */
    private StringToken nextStringLiteral(char startQuote) {
        // Contents of the string literal
        StringBuilder builder = new StringBuilder();

        while (true) {
            int read = read();

            if (read == startQuote) {
                break;
            } else if (read == '\\') {
                builder.append(nextStringEscapeSequence());
            } else if (read == -1) {
                throw new IllegalStateException("Unexpected EOF in string literal");
            } else {
                builder.append((char) read);
            }
        }

        StringToken.QuoteType quoteType = startQuote == '"' ?
                StringToken.QuoteType.DOUBLE : StringToken.QuoteType.SINGLE;

        return new StringToken(
                builder.toString(), quoteType, advanceRecorder(), currentPosition
        );
    }

    /**
     * Gets the character corresponding to the next
     * escape sequence.
     * @return The next escape sequence's character value
     */
    private char nextStringEscapeSequence() {
        int chr = read();

        switch (chr) {
            // single-letter sequences
            case 'b': return '\b';
            case 't': return '\t';
            case 'n': return '\n';
            case 'f': return '\f';
            case 'r': return '\r';

            // special characters
            case '\'': return '\'';
            case '\\': return '\\';

            // unicode character
            case 'u': {
                int hex = 0;

                // read 4 hex digits (0-9, a-f, A-F)
                for (int i = 0; i < 4; i++) {
                    int ord = read();

                    if (ord >= '0' && ord <= '9') {
                        // set value to numeric value
                        // of the character
                        ord -= '0';
                    } else if (ord >= 'a' && ord <= 'f') {
                        // add back 10 so a = 10
                        ord -= ('a' - 10);
                    } else if (ord >= 'A' && ord <= 'F') {
                        // add back 10 so A = 10
                        ord -= ('A' - 10);
                    } else {
                        throw new InvalidStateException(
                                "Invalid hexadecimal digit: " + (char) ord
                        );
                    }

                    // append value to hex number
                    hex = hex * 16 + ord;
                }
            }

            // octal literal
            // maximum value 377 (255 dec)
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7': {
                int ord = chr - '0';
                if (ord <= 3) {
                    int peek = peek();

                    for (int i = 0; i < 2; i++) {
                        if (peek >= '0' && peek <= '7') {
                            read();
                            ord = ord * 8 + (peek - '0');
                            peek = peek();
                        }
                    }

                }
                return (char) ord;
            }

            default:
                throw new IllegalStateException(
                        "Unexpeced start of escape sequence: " + chr
                );
        }
    }

    private Token nextIdentifier(char first) {
        StringBuilder sb = new StringBuilder().append(first);

        while (peek() > -1 && Chars.isIdentifierRest((char) peek())) {
            sb.append((char) read());
        }

        String identifier = sb.toString();
        if (KEYWORD_MAP.containsKey(identifier)) {
            if (identifier.equals("is") || identifier.equals("isnt")) {
                Token next = nextToken();

                if (next.getType() == IDENTIFIER && next.getContent().equals("a")) {
                    Type type = identifier.equals("is") ? INSTANCEOF_KEYWORD : NOT_INSTANCEOF_KEYWORD;
                    return newToken(type);
                } else {
                    queuedTokens.push(next);
                }
            }
        }

        return newToken(IDENTIFIER, identifier);
    }

    private Token nextNumber(char first) {
        StringBuilder sb = new StringBuilder().append(first);

        while (peek() > -1 && Chars.isDigit((char) peek())) {
            sb.append((char) read());
        }

        Number num = Integer.valueOf(sb.toString());
        return new NumberToken(num, advanceRecorder(), currentPosition);
    }

    // Methods for differentiating tokens

    private Token diffPlus() {
        if (accept('+')) {
            return newToken(INC); // ++
        }
        return newToken(PLUS); // +
    }

    private Token diffMin() {
        if (accept('-')) {
            return newToken(DEC); // --
        }
        return newToken(MIN); // -
    }

    private Token diffDots() {
        if (accept('.')) {
            if (accept('.')) {
                return newToken(RANGE_INCLUSIVE); // ...
            }
            return newToken(RANGE_EXCLUSIVE); // ..
        }
        return newToken(DOT); // .
    }

    private Token diffColon() {
        if (accept(':')) {
            return newToken(SEP); // ::
        }
        return newToken(COLON); // :
    }

    private Token diffGT() {
        if (accept('=')) {
            return newToken(GTE); // >=
        } else if (accept('>')) {
            if (accept('>')) {
                return newToken(U_SH_R); // ">>>";
            }
            return newToken(SH_R); // ">>";
        }
        return newToken(GT); // ">"
    }

    private Token diffLT() {
        if (accept('=')) {
            return newToken(LTE); // "<="
        } else if (accept('<')) {
            return newToken(SH_L); // "<<"
        }
        return newToken(LT); // "<"
    }

    private Token diffOr() {
        if (accept('|')) {
            return newToken(LOGIC_OR); //
        }
        return newToken(BIT_OR);
    }

    private Token diffAnd() {
        if (accept('&')) {
            return newToken(LOGIC_AND);
        }
        return newToken(BIT_AND);
    }

    private Token diffBang() {
        if (accept('=')) {
            return newToken(NE); // !=
        }
        return newToken(NOT); // !
    }

    private Token diffEq() {
        if (accept('=')) {
            return newToken(EQ); // ==
        }
        return newToken(ASSIGN); // =
    }
}
