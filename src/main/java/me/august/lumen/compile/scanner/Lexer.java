package me.august.lumen.compile.scanner;

import me.august.lumen.common.Chars;
import me.august.lumen.compile.Driver;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.error.SourcePositionProvider;
import me.august.lumen.compile.scanner.tokens.ImportPathToken;
import me.august.lumen.compile.scanner.tokens.NumberToken;
import me.august.lumen.compile.scanner.tokens.StringToken;

import java.io.*;
import java.util.*;

import static me.august.lumen.compile.scanner.Type.*;

public class Lexer implements Iterable<Token>, SourcePositionProvider {

    private static final Map<String, Type> KEYWORDS = new HashMap<>();
    private final List<String> ignore;

    static {
        // looks better than map.put(...) x 100
        Object[][] pairs = {
            {"def", DEF_KEYWORD},
            {"import", IMPORT_KEYWORD},
            {"class", CLASS_KEYWORD},
            {"instanceof", INSTANCEOF_KEYWORD}, // to be replaced with `is a` when the lexer supports it
            {"var", VAR_KEYWORD},
            {"if", IF_KEYWORD},
            {"unless", UNLESS_KEYWORD},
            {"else", ELSE_KEYWORD},
            {"while", WHILE_KEYWORD},
            {"until", UNTIL_KEYWORD},
            {"break", BREAK_KEYWORD},
            {"next", NEXT_KEYWORD},
            {"stc", STATIC_KEYWORD},
            {"static", STATIC_KEYWORD},
            {"as", CAST_KEYWORD},
            {"then", THEN_KEYWORD},
            {"return", RETURN_KEYWORD},
            {"rescue", RESCUE_KEYWORD},

            {"is", EQ},
            {"isnt", NE},
            {"and", LOGIC_AND},
            {"or", LOGIC_OR},

            {"pb", ACC_PUBLIC},
            {"public", ACC_PUBLIC},
            {"pv", ACC_PRIVATE},
            {"private", ACC_PRIVATE},
            {"pt", ACC_PROTECTED},
            {"protected", ACC_PROTECTED},
            {"pk", ACC_PACKAGE},
            {"package_private", ACC_PACKAGE},

            {"true", TRUE},
            {"yes", TRUE},
            {"on", TRUE},
            {"false", FALSE},
            {"no", FALSE},
            {"off", FALSE},
            {"null", NULL},
            {"nil", NULL}
        };
        for (Object[] pair : pairs) {
            KEYWORDS.put((String) pair[0], (Type) pair[1]);
        }
    }

    private Reader reader;
    private int pos;
    private BuildContext build;

    private Stack<Token> queued = new Stack<>();

    public Lexer(Reader reader) {
        this.reader = reader;
        this.build = new Driver.CompileBuildContext();
        this.ignore = new ArrayList<>();
    }

    public Lexer(String src) {
        this(new StringReader(src));
    }

    public Lexer(InputStream in) {
        this(new InputStreamReader(in));
    }

    /**
     * Tells the lexer to ignore the specified identifier
     * @param identifier
     */
    public void ignore(String identifier) {
        ignore.add(identifier);
    }

    /**
     * Read one character from input
     *
     * @return The next character's ordinal value, or -1 for EOF
     */
    private int read() {
        try {
            pos++;
            return reader.read();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read character: " + e.getMessage(), e);
        }
    }

    /**
     * Peeks one character ahead of the current position
     *
     * @return The next character's ordinal value, or -1 for EOF
     */
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

    private void mark(int n) {
        try {
            reader.mark(n);
        } catch (IOException ignored) {}
    }

    private void reset() {
        try {
            reader.reset();
        } catch (IOException ignored) {}
    }

    /**
     * Gets the next token from input
     *
     * @return The next token
     */
    public Token nextToken() {
        if (!queued.empty()) return queued.pop();

        while (true) {
            int read = read();
            if (read == -1) return token(EOF);
            char c = (char) read;

            if (c == '(') return token(L_PAREN);
            else if (c == ')') return token(R_PAREN);
            else if (c == '{') return token(L_BRACE);
            else if (c == '}') return token(R_BRACE);
            else if (c == '[') return token(L_BRACKET);
            else if (c == ']') return token(R_BRACKET);

            else if (c == ',') return token(COMMA);
            else if (c == '.') return nextDots();
            else if (c == ':') return nextColonOrSep();

            else if (c == '+') return nextPlusOrIncOrNumber();
            else if (c == '-') return nextMinOrDecOrNumber();
            else if (c == '*') return token(MULT);
            else if (c == '/') return token(DIV);
            else if (c == '%') return token(REM);

            else if (c == '>') return nextGtOrGteOrShift();
            else if (c == '<') return nextLtOrLteOrShift();

            else if (c == '|') return nextOr();
            else if (c == '&') return nextAnd();
            else if (c == '~') return token(BIT_COMP);

            else if (c == '!') return nextNotOrNe();
            else if (c == '?') return token(QUESTION);

            else if (c == '=') return nextEqOrAssign();

            else if (Chars.isAlpha(c)) return nextIdent(c);
            else if (Chars.isDigit(c)) return nextNumber(c, false);
            else if (c == '"' || c == '\'') return nextString(c);

            else if (c == '#') consumeComment();
            else if (c == ' ' || c == '\r' || c == '\n' || c == '\t') {
                continue; // ignore whitespace chars
            } else {
                build.error("Unexpected character: " + c, this);
            }
        }
    }

    /**
     * Constructs a token with the appropriate position
     * and given type
     *
     * @param type The token's type
     * @return The new token
     */
    private Token token(Type type) {
        return new Token(null, pos, pos + 1, type);
    }

    /**
     * Gets the next String identifier from input,
     * following this regular expression:
     * [a-zA-Z_][\w]*
     *
     * @return The next String identifier
     */
    private String ident() {
        StringBuilder sb = new StringBuilder();

        while (true) {
            int peek = peek();
            if (peek < 0) break;
            char c = (char) peek;

            if (Chars.isAlpha(c) || (sb.length() > 1 && Chars.isDigit(c))) {
                sb.append(c);
                read();
            } else {
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Gets the next token who's string form is an identifier
     *
     * @param firstChar The identifier's first character
     * @return A token with the IDENTIFIER type, or a keyword
     * token if the identifier is defined in the KEYWORDS map
     */
    private Token nextIdent(char firstChar) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstChar);

        int startPos = pos;
        sb.append(ident());
        int endPos = pos;

        String ident = sb.toString();

        if (ignore.contains(ident)) {
            return nextToken();
        }

        Token token = new Token(ident, startPos, endPos, null);
        Type type = KEYWORDS.get(ident);
        if (type == null) {
            type = IDENTIFIER;
        } else {
            handleKeyword(type);
            if (ident.equals("is") || ident.equals("isnt")) {
                consumeWhitespace();
                Token next = nextToken();

                // next token is identifier with content 'a'
                if (next.getType() == IDENTIFIER && next.getContent().equals("a")) {
                    type = ident.equals("is") ? INSTANCEOF_KEYWORD : NOT_INSTANCEOF_KEYWORD;
                } else {
                    queued.push(next);
                }
            }
        }
        token.setType(type);

        return token;
    }

    /**
     * Called when a keyword token is read
     *
     * @param keyword The keyword token's type
     */
    private void handleKeyword(Type keyword) {
        if (keyword == IMPORT_KEYWORD) {
            handleImport();
        }
    }

    /**
     * Called when a token with a IMPORT_KEYWORD
     * type is read
     */
    private void handleImport() {
        read(); // consume whitespace

        int startPos = pos;

        StringBuilder sb = new StringBuilder();
        sb.append(ident());
        List<String> nodes = null;

        boolean didEnd = false;
        while (peek() == '.') {
            sb.append((char) read());

            if (didEnd)
                // TODO proper exception handling
                throw new RuntimeException("import statement already terminated");

            if (peek() == '{') {
                read();
                // once we reach a multi-import, it must be the end
                didEnd = true;

                consumeWhitespace();

                nodes = new ArrayList<>();
                nodes.add(ident());
                consumeWhitespace();

                while (peek() == ',') {
                    read(); // read ','
                    consumeWhitespace();
                    nodes.add(ident());
                }

                // TODO proper exception handling
                if (read() != '}') throw new RuntimeException("Expected right brace: }");
            } else {
                sb.append(ident());
            }
        }

        String importPath = sb.toString();
        if (nodes == null) {
            // grab last identifier after dot
            int lastIdx = importPath.lastIndexOf('.');
            nodes       = Arrays.asList(importPath.substring(lastIdx + 1));
            importPath  = importPath.substring(0, lastIdx);
        }

        int endPos = pos;
        queued.push(new ImportPathToken(
            sb.toString(), startPos, endPos, importPath, nodes
        ));
    }

    private Token nextDots() {
        if (peek() == '.') {
            read();
            return token(RANGE);
        } else {
            return token(DOT);
        }
    }

    /**
     * The next MIN token or negative NUMBER token.
     * @return A MIN or NUMBER token.
     */
    private Token nextMinOrDecOrNumber() {
        if (Character.isDigit(peek())) {
            return nextNumber((char) read(), true);
        } else if (peek() == '-') {
            read();
            return token(DEC);
        } else if (peek() == '>') {
            read();
            return token(R_ARROW);
        } else {
            return token(MIN);
        }
    }

    /**
     * The next PLUS token or positive NUMBER token.
     * @return A PLUS or NUMBER token.
     */
    private Token nextPlusOrIncOrNumber() {
        if (Character.isDigit(peek())) {
            return nextNumber((char) read(), false);
        } else if (peek() == '+') {
            read();
            return token(INC);
        } else {
            return token(PLUS);
        }
    }

    /**
     * Gets the next token in the form of a number
     *
     * @param firstDigit The number's first digit
     * @return A token with the NUMBER type
     */
    // TODO possibly refactor this mega-method
    private Token nextNumber(char firstDigit, boolean neg) {
        // read prefix
        NumericPrefix prefix = readPrefix(firstDigit);
        StringBuilder sb = new StringBuilder();

        if (neg) sb.append('-');
        if (prefix != NumericPrefix.HEX && prefix != NumericPrefix.BIN)
            sb.append(firstDigit);

        int startPos = pos;
        int endPos = pos;

        // has decimal point
        boolean hasDP = false;

        while (true) {
            int peek = peek();
            if (peek == -1) break;
            char c = (char) peek;

            if (Chars.isDigit(c) || c == '.') {
                if (c == '.') {
                    if (hasDP)
                        // TODO proper exception handling
                        throw new RuntimeException("Already has decimal point");
                    if (prefix != NumericPrefix.NONE)
                        // TODO proper exception handling
                        throw new RuntimeException("Unexpected prefix and decimal point combination");
                    hasDP = true;
                }
                sb.append(c);
                read();
                endPos++;
            } else {
                break;
            }
        }

        // Read exponent
        boolean hasExp = false;
        StringBuilder exp = null;

        if (peek() == 'e' || peek() == 'E') {
            endPos++;
            hasExp = true;
            exp = new StringBuilder("e");
            read(); // consume 'e'
            while (Character.isDigit(peek())) {
                endPos++;
                exp.append((char) read());
            }
        }

        NumericSuffix suffix = readSuffix();

        Number val = null;
        if (!hasDP) {
            long temp = Long.parseLong(sb.toString());
            temp = prefix.convertBase(temp);
            if (temp > Integer.MAX_VALUE || temp < Integer.MIN_VALUE || suffix == NumericSuffix.LONG) {
                val = temp;
            } else if (suffix == NumericSuffix.FLOAT || suffix == NumericSuffix.DOUBLE) {
                hasDP = true;
            } else {
                val = (int) temp;
            }
        }
        if (hasDP) {
            if (hasExp) sb.append(exp);
            double temp = Double.parseDouble(sb.toString());
            if (suffix == NumericSuffix.DOUBLE || suffix == NumericSuffix.NONE) {
                val = temp;
            } else {
                val = (float) temp;
            }
        }

        return new NumberToken(val, startPos, endPos);
    }

    /**
     * Reads a numeric prefix:
     * 0x, 0X:   HEX
     * 0b, 0B:   BIN
     * 0[digit]: OCT
     * else:     NONE
     *
     * @param first The first character
     * @return The next numeric prefix
     */
    private NumericPrefix readPrefix(char first) {
        mark(2);
        String s = String.valueOf(first) + (char) read();
        NumericPrefix prefix;
        switch (s) {
            case "0x": case "0X":
                prefix = NumericPrefix.HEX;
                reset(); read();
                break;
            case "0b":case "0B":
                prefix = NumericPrefix.BIN;
                reset(); read();
                break;
            default:
                if (s.charAt(0) == '0' && Character.isDigit(s.charAt(1))) {
                    prefix = NumericPrefix.OCT;
                } else {
                    prefix = NumericPrefix.NONE;
                }
                reset();
        }
        return prefix;
    }

    /**
     * Reads a numeric suffix:
     * l, L: LONG
     * f, F: FLOAT
     * d, D: DOUBLE
     * else: NONE
     *
     * @return The next numeric suffix
     */
    private NumericSuffix readSuffix() {
        int chr = Character.toUpperCase(peek());
        switch (chr) {
            case 'L': return NumericSuffix.LONG;
            case 'F': return NumericSuffix.FLOAT;
            case 'D': return NumericSuffix.DOUBLE;
            default : return NumericSuffix.NONE;
        }
    }

    /**
     * Gets the next token in the form of a
     * double-quote (") or single-quote (') delimited string.
     *
     * @return A token with the STRING type
     */
    private Token nextString(char quote) {
        StringBuilder sb = new StringBuilder();

        int startPos = pos;
        int endPos = pos;

        while (true) {
            int read = read();
            if (read == -1) throw new RuntimeException("Unexpected EOF in String literal");

            if (read == quote) {
                break;
            } else {
                sb.append((char) read);
                endPos++;
            }
        }

        return new StringToken(sb.toString(), (quote == '"' ? StringToken.QuoteType.DOUBLE : StringToken.QuoteType.SINGLE), startPos, endPos);
    }

    /**
     * Precondition: last read char was '='
     * <p>
     * Differentiates the following tokens:
     * EQ      "==" (equality operator)
     * ASSIGN  "="  (assignment operator)
     *
     * @return The next EQ or ASSIGN type token
     */
    private Token nextEqOrAssign() {
        if (peek() == '=') { // "=="
            read(); // consume '='
            return token(EQ);
        }
        return token(ASSIGN);
    }

    /**
     * Precondition: last read char was '>'
     * <p>
     * Differentiates the following tokens:
     * GT      ">"   (greater than)
     * GTE     ">="  (greater than or equal to)
     * SH_R    ">>"  (right bitshift)
     * U_SH_R  ">>>" (unsigned right bitshift)
     *
     * @return The next GT or GTE or SH_R or U_SH_R type token
     */
    private Token nextGtOrGteOrShift() {
        Type ty;
        if (peek() == '=') {
            read(); // consume '='
            ty = GTE;
        } else if (peek() == '>') {
            read(); // consume 2nd '>'
            if (peek() == '>') {
                read(); // consume 3rd '>'
                ty = U_SH_R;
            } else {
                ty = SH_R;
            }
        } else {
            ty = GT;
        }
        return token(ty);
    }

    /**
     * Precondition: last read char was '<'
     * <p>
     * Differentiates the following tokens:
     * LT   "<"  (less than)
     * LTE  "<=" (less than or equal to)
     * SH_L "<<" (left bitshift)
     *
     * @return The next LT or LTE or SH_L type token
     */
    private Token nextLtOrLteOrShift() {
        Type ty;
        if (peek() == '=') {
            read(); // consume '='
            ty = LTE;
        } else if (peek() == '<') {
            read(); // consume 2nd '<'
            ty = SH_L;
        } else {
            ty = LT;
        }
        return token(ty);
    }

    /**
     * Precondition: last read char was '|'
     * <p>
     * Differentiates the following tokens:
     * LOGIC_OR  "||" (logical OR)
     * BIT_OR    "|"  (bitwise OR)
     *
     * @return The next LOGIC_OR or BIT_OR type token
     */
    private Token nextOr() {
        if (peek() == '|') {
            read(); // consume '|'
            return token(LOGIC_OR);
        }
        return token(BIT_OR);
    }

    /**
     * Precondition: last read char was '&'
     * <p>
     * Differentiates the following tokens:
     * LOGIC_AND  "&&" (logical AND)
     * BIT_AND    "&"  (bitwise AND)
     *
     * @return The next LOGIC_AND or BIT_AND type token
     */
    private Token nextAnd() {
        if (peek() == '&') {
            read(); // consume '&'
            return token(LOGIC_AND);
        }
        return token(BIT_AND);
    }

    /**
     * Precondition: last read char was '!'
     * <p>
     * Differentiates the following tokens:
     * NOT  "!"  (not, invert)
     * NE   "!=" (not equal to)
     *
     * @return The next NOT or NE type token
     */
    private Token nextNotOrNe() {
        if (peek() == '=') {
            read(); // consume '='
            return token(NE);
        }
        return token(NOT);
    }

    /**
     * Precondition: last read char was ':'
     * <p>
     * Differentiates the following tokens:
     * COLON  ":"
     * SEP    "::"
     *
     * @return The next COLON or SEP type token
     */
    private Token nextColonOrSep() {
        if (peek() == ':') {
            read(); // consume ':'
            return token(SEP);
        }
        return token(COLON);
    }

    /**
     * Consumes a single-line comment (reads until
     * '\n' is reached) or a multi-line comment (starts
     * with '#*' and ends with '*#').
     */
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

    private void consumeWhitespace() {
        while (peek() == ' ') read();
    }

    /**
     * Iterates over all tokens returned by
     * this Lexer until EOF is reached
     *
     * @return The token iterator
     */
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

    /**
     * The current reading position
     *
     * @return The current reading position
     */
    @Override
    public int getStart() {
        return pos;
    }

    /**
     * The current reading position plus one
     *
     * @return The current reading position plus one
     */
    @Override
    public int getEnd() {
        return pos + 1;
    }

    /**
     * Represents a numerical prefix. Modifies
     * the base of the following number.
     * HEX:  hexadecimal, base 16
     * OCT:  octal, base 8
     * BIN:  binary, base 2
     * NONE: no prefix (decimal, base 10)
     */
    private enum NumericPrefix {
        HEX(16), OCT(8), BIN(2), NONE(10);

        private int radix;
        NumericPrefix(int radix) {
            this.radix = radix;
        }

        public long convertBase(long n) {
            if (this == NONE) return n;
            return Long.valueOf(Long.toString(n), radix);
        }
    }

    private enum NumericSuffix {
        LONG, FLOAT, DOUBLE, NONE
    }
}
