package me.august.lumen.compile.scanner;

import me.august.lumen.common.Chars;
import me.august.lumen.compile.Driver;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.error.SourcePositionProvider;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import static me.august.lumen.compile.scanner.Type.*;

public class Lexer implements Iterable<Token>, SourcePositionProvider {

    private static final Map<String, Type> KEYWORDS = new HashMap<>();

    static {
        // looks better than map.put(...) x 100
        Object[][] pairs = {
            {"def", DEF_KEYWORD},
            {"import", IMPORT_KEYWORD},
            {"class", CLASS_KEYWORD},
            {"is", IS_KEYWORD},
            {"var", VAR_KEYWORD},
            {"if", IF_KEYWORD},
            {"else", ELSE_KEYWORD},
            {"while", WHILE_KEYWORD},
            {"stc", STATIC_KEYWORD},
            {"static", STATIC_KEYWORD},

            {"pb", ACC_PUBLIC},
            {"public", ACC_PUBLIC},
            {"pv", ACC_PRIVATE},
            {"private", ACC_PRIVATE},
            {"pt", ACC_PROTECTED},
            {"protected", ACC_PROTECTED},
            {"pk", ACC_PACKAGE},
            {"package_private", ACC_PACKAGE},

            {"true", TRUE},
            {"false", FALSE},
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
    }

    public Lexer(String src) {
        this(new StringReader(src));
    }

    public Lexer(InputStream in) {
        this(new InputStreamReader(in));
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
            else if (c == '.') return token(DOT);
            else if (c == ':') return nextColonOrSep();

            else if (c == '+') return token(PLUS);
            else if (c == '-') return token(MIN);
            else if (c == '*') return token(MULT);
            else if (c == '/') return token(DIV);

            else if (c == '>') return nextGtOrGteOrShift();
            else if (c == '<') return nextLtOrLteOrShift();

            else if (c == '|') return nextOr();
            else if (c == '&') return nextAnd();

            else if (c == '!') return nextNegOrNe();
            else if (c == '?') return token(QUESTION);

            else if (c == '=') return nextEqOrAssign();

            else if (Chars.isAlpha(c)) return nextIdent(c);
            else if (Chars.isDigit(c)) return nextNumber(c);
            else if (c == '"') return nextString();

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

        Token token = new Token(ident, startPos, endPos, null);
        Type type = KEYWORDS.get(ident);
        if (type == null) {
            type = IDENTIFIER;
        } else {
            handleKeyword(type);
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

        while (peek() == '.') {
            sb.append((char) read());
            sb.append(ident());
        }

        int endPos = pos;
        String importPath = sb.toString();
        queued.push(new Token(importPath, startPos, endPos, IMPORT_PATH));
    }

    /**
     * Gets the next token in the form of a number
     *
     * @param firstDigit The number's first digit
     * @return A token with the NUMBER type
     */
    private Token nextNumber(char firstDigit) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstDigit);

        int startPos = pos;
        int endPos = pos;

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

    /**
     * Gets the next token in the form of a
     * double-quote (") delimited string.
     *
     * @return A token with the STRING type
     */
    private Token nextString() {
        StringBuilder sb = new StringBuilder();

        int startPos = pos;
        int endPos = pos;

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
     * NEG  "!"  (negate)
     * NE   "!=" (not equal to)
     *
     * @return The next NEG or NE type token
     */
    private Token nextNegOrNe() {
        if (peek() == '=') {
            read(); // consume '='
            return token(NE);
        }
        return token(NEG);
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

    private void consumeComment() {
        // read until we hit a newline
        // noinspection StatementWithEmptyBody
        while (read() != '\n');
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
     * @returnThe current reading position plus one
     */
    @Override
    public int getEnd() {
        return pos + 1;
    }
}
