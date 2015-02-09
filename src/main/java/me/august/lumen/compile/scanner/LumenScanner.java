package me.august.lumen.compile.scanner;

import me.august.lumen.common.Chars;
import me.august.lumen.compile.scanner.tokens.ImportPathToken;
import me.august.lumen.compile.scanner.tokens.NumberToken;
import me.august.lumen.compile.scanner.tokens.StringToken;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.function.Consumer;

import static me.august.lumen.compile.scanner.Type.*;

public class LumenScanner implements TokenSource {

    private static Map<String, Type> KEYWORD_MAP = new HashMap<>();

    // initialize keyword map
    static {
        for (Type type : Type.values()) {
            if (type.getKeywords() == null) continue;;
            for (String keyword : type.getKeywords()) {
                KEYWORD_MAP.put(keyword, type);
            }
        }
    }

    private static Map<Type, Consumer<LumenScanner>> KEYWORD_HANDLERS = new HashMap<>();

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
            int peek = reader.read();
            reader.reset();

            return peek;
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

    private void consumeWhitespace() {
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
        if (!queuedTokens.empty())
            return queuedTokens.pop();

        while (true) {
            int chr = read();

            switch (chr) {
                // End of file (or error) reached
                case -1: return newToken(EOF);

                // Bracket tokens
                case '(': return newToken(L_PAREN);
                case ')': return newToken(R_PAREN);
                case '{': return newToken(L_BRACE);
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

                case '#': consumeComment();

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
                        throw new IllegalStateException(
                                "Invalid hexadecimal digit: " + (char) ord
                        );
                    }

                    // append value to hex number
                    hex = hex * 16 + ord;
                }

                return (char) hex;
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
        String identifier = nextPlainIdentifier(first);
        System.out.println(">> GOT IDENTIFIER -> " + identifier);

        if (identifier.equals("is") || identifier.equals("isnt")) {
            Token next = nextToken();

            if (next.getType() == IDENTIFIER && next.getContent().equals("a")) {
                Type type = identifier.equals("is") ? INSTANCEOF_KEYWORD : NOT_INSTANCEOF_KEYWORD;
                return newToken(type);
            } else {
                queuedTokens.push(next);
            }
        }

        if (KEYWORD_MAP.containsKey(identifier)) {
            Token tok = newToken(KEYWORD_MAP.get(identifier));
            if (KEYWORD_HANDLERS.containsKey(tok.getType())) {
                KEYWORD_HANDLERS.get(tok.getType()).accept(this);
            }

            return tok;
        }

        return newToken(IDENTIFIER, identifier);
    }

    private String nextPlainIdentifier(char first) {
        return nextPlainIdentifier().insert(0, first).toString();
    }

    private StringBuilder nextPlainIdentifier() {
        StringBuilder sb = new StringBuilder();

        while (peek() > -1 && Chars.isIdentifierRest((char) peek())) {
            sb.append((char) read());
        }

        return sb;
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

    private Token nextNumber(char first) {
        StringBuilder sb = new StringBuilder();
        int base = getPrefixBase(first);

        if (base != 16 && base != 2) {
            sb.append(first);
        }

        while (peek() > -1 && isValidChar((char) peek(), base)) {
                //(Chars.isDigit((char) peek()) || Chars.isAlpha((char) peek()))) {
            if (!isValidChar((char) peek(), base)) {
                throw new RuntimeException("Illegal digit: " + (char) peek());
            }

            sb.append((char) read());
        }

        if (peek() == '.') {
            sb.append((char) read());
            int peek = peek();
            while (peek > -1 && Chars.isDigit((char) peek)) {
                sb.append((char) read());
                peek = peek();
            }
        }

        if (peek() == 'e') {
            sb.append((char) read());
            if (peek() == '+' || peek() == '-') {
                sb.append((char) read());
            }
            while (Chars.isDigit((char) peek())) {
                sb.append((char) read());
            }
        }

        Class<? extends Number> type = getSuffixType();



        Number num = parseNumber(sb.toString(), base, type);
        return new NumberToken(num, advanceRecorder(), currentPosition);
    }

    // Numbers
    private int getPrefixBase(char chr) {
        if (chr > '9' || chr < '0') {
            return -1;
        }

        if (chr != '0') {
            return 10;
        }

        switch (peek()) {
            case 'x':
            case 'X':
                read();
                return 16;
            case 'b':
            case 'B':
                read();
                return 2;
            default:
                return 8;
        }
    }

    private Class<? extends Number> getSuffixType() {
        switch (peek()) {
            case 'f':
            case 'F':
                read();
                return Float.class;
            case 'd':
            case 'D':
                read();
                return Double.class;
            case 'l':
            case 'L':
                read();
                return Long.class;
            default:
                return null;
        }
    }

    private boolean isValidChar(char chr, int radix) {
        return Character.digit(chr, radix) > -1;
    }

    private Number parseNumber(String num, int base, Class<? extends Number> type) {
        if (base != 10 && (num.contains(".") || num.contains("e"))) {
            throw new RuntimeException("Illegal base prefix");
        }

        Number result;

        if (num.contains(".") || num.contains("e")) {
            result = Double.valueOf(num);
        } else {
            Long converted = Long.parseLong(num, base);
            if (converted <= Integer.MAX_VALUE && converted >= Integer.MIN_VALUE) {
                result = converted.intValue();
            } else {
                result = converted;
            }
        }

        if (type == Double.class) {
            return result.doubleValue();
        } else if (type == Float.class) {
            return result.floatValue();
        } else if (type == Long.class) {
            if (result.getClass() == Long.class || result.getClass() == Integer.class) {
                return result.longValue();
            } else {

                throw new RuntimeException("Illegal long suffix");
            }
        } else {
            return result;
        }
    }

    private void consumeComment() {
        if (peek() == '*') {
            read();
            // noinspection StatementWithEmptyBody
            while (!(read() == '*' && peek() == '#'));
            read();
        } else {
            // noinspection StatementWithEmptyBody
            while (read() != '\n');
        }
    }

    static {
        KEYWORD_HANDLERS.put(Type.IMPORT_KEYWORD, (lex) -> {
            lex.read(); // consume whitespace

            StringBuilder sb = new StringBuilder();
            sb.append(lex.nextPlainIdentifier());
            List<String> nodes = null;

            boolean didEnd = false;
            while (lex.peek() == '.') {
                sb.append((char) lex.read());

                if (didEnd)
                    // TODO proper exception handling
                    throw new RuntimeException("import statement already terminated");

                if (lex.accept('{')) {
                    // once we reach a multi-import, it must be the end
                    didEnd = true;

                    lex.consumeWhitespace();

                    nodes = new ArrayList<>();
                    nodes.add(lex.nextPlainIdentifier().toString());
                    lex.consumeWhitespace();

                    while (lex.accept(',')) {
                        lex.consumeWhitespace();
                        nodes.add(lex.nextPlainIdentifier().toString());
                    }

                    // TODO proper exception handling
                    if (lex.read() != '}')
                        throw new RuntimeException("Expected right brace: }");
                } else {
                    sb.append(lex.nextPlainIdentifier());
                }
            }

            String importPath = sb.toString();
            if (nodes == null) {
                // grab last identifier after dot
                int lastIdx = importPath.lastIndexOf('.');
                nodes       = Arrays.asList(importPath.substring(lastIdx + 1));
                importPath  = importPath.substring(0, lastIdx);
            }

            lex.queuedTokens.push(new ImportPathToken(
                    sb.toString(), lex.advanceRecorder(), lex.lastRecordedPosition,
                    importPath, nodes
            ));

        });
    }

}
