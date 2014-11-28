package me.august.lumen.compile.scanner;

import me.august.lumen.common.Chars;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import static me.august.lumen.compile.scanner.Type.*;

public class Lexer implements Iterable<Token> {

    private static final Map<String, Type> KEYWORDS = new HashMap<>();

    static {
        // looks better than map.put(...) x 100
        Object[][] pairs = {
            {"def",     DEF_KEYWORD             },
            {"import",  IMPORT_KEYWORD          },
            {"class",   CLASS_KEYWORD           },
            {"is",      IS_KEYWORD              },
            {"var",     VAR_KEYWORD             },
            {"if",      IF_KEYWORD              },
            {"else",    ELSE_KEYWORD            },
            {"while",   WHILE_KEYWORD           },

            {"pb",      ACC_PUBLIC              },
            {"public",  ACC_PUBLIC              },
            {"pv",      ACC_PRIVATE             },
            {"private", ACC_PRIVATE             },
            {"pt",      ACC_PROTECTED           },
            {"protected",       ACC_PROTECTED   },
            {"pk",              ACC_PACKAGE     },
            {"package_private", ACC_PACKAGE     },

            {"true",    TRUE                    },
            {"false",   FALSE                   },
            {"null",    NULL                    },
            {"nil",     NULL                    }
        };
        for (Object[] pair : pairs) {
            KEYWORDS.put((String) pair[0], (Type) pair[1]);
        }
    }

    private Reader reader;
    private int pos;

    private Stack<Token> queued = new Stack<>();

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
        if (!queued.empty()) return queued.pop();

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

            else if (c == ',') return token(COMMA);
            else if (c == ':') return token(COLON);

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

            else if (c == ' ' || c == '\r' || c == '\n' || c =='\t') {
                continue; // ignore whitespace chars
            } else {
                throw new RuntimeException("Unexpected character: " + c);
            }
        }
    }

    private Token token(Type type) {
        return new Token(null, pos, pos + 1, type);
    }

    // Gets the next identifier, following this pattern:
    // [a-zA-Z_]\w*
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

    private Token nextIdent(char firstChar) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstChar);

        int startPos = pos;
        sb.append(ident());
        int endPos   = pos;

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

    private void handleKeyword(Type keyword) {
        if (keyword == IMPORT_KEYWORD) {
            handleImport();
        }
    }

    private void handleImport() {
        read(); // consume whitespace

        int startPos = pos;

        StringBuilder sb = new StringBuilder();
        sb.append(ident());

        while(peek() == '.') {
            sb.append((char) read());
            sb.append(ident());
        }

        int endPos = pos;
        String importPath = sb.toString();
        queued.push(new Token(importPath, startPos, endPos, IMPORT_PATH));
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

    /*
    Precondition: last read char was '='

    Differentiates the following tokens:
    EQ      "==" (equality operator)
    ASSIGN  "="  (assignment operator)
     */
    private Token nextEqOrAssign() {
        if (peek() == '=') { // "=="
            read(); // consume '='
            return token(EQ);
        }
        return token(ASSIGN);
    }

    /*
    Precondition: last read char was '>'

    Differentiates the following tokens:
    GT      ">"   (greater than)
    GTE     ">="  (greater than or equal to)
    SH_R    ">>"  (right bitshift)
    U_SH_R  ">>>" (unsigned right bitshift)
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

    /*
    Precondition: last read char was '<'

    Differentiates the following tokens:
    LT   "<"  (less than)
    LTE  "<=" (less than or equal to)
    SH_L "<<" (left bitshift)
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

    /*
    Precondition: last read char was '|'

    Differentiates the following tokens:
    LOGIC_OR  "||" (logical OR)
    BIT_OR    "|"  (bitwise OR)
     */
    private Token nextOr() {
        if (peek() == '|') {
            read(); // consume '|'
            return token(LOGIC_OR);
        }
        return token(BIT_OR);
    }

    /*
    Precondition: last read char was '&'

    Differentiates the following tokens:
    LOGIC_AND  "&&" (logical AND)
    BIT_AND    "&"  (bitwise AND)
     */
    private Token nextAnd() {
        if (peek() == '&') {
            read(); // consume '&'
            return token(LOGIC_AND);
        }
        return token(BIT_AND);
    }

    /*
    Precondition: last read char was '!'

    Differentiates the following tokens:
    NEG  "!"  (negate)
    NE   "!=" (not equal to)
     */
    private Token nextNegOrNe() {
        if (peek() == '=') {
            read(); // consume '='
            return token(NE);
        }
        return token(NEG);
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
