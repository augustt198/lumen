package me.august.lumen;

import me.august.lumen.compile.scanner.LumenScanner;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.TokenSource;
import me.august.lumen.compile.scanner.Type;
import me.august.lumen.compile.scanner.tokens.ImportPathToken;
import me.august.lumen.compile.scanner.tokens.NumberToken;
import org.junit.Assert;
import org.junit.Test;

import static me.august.lumen.compile.scanner.Type.*;

public class LexerTest {

    private static final String TOKEN_TEST_FILE = "/token_test.txt";

    private static TokenSource createLexer(String source) {
        return new LumenScanner(source);
    }

    @Test
    public void testTokens() {
        Type[] expectedTypes = {
            IDENTIFIER,
            GT, GTE, SH_R, U_SH_R,
            LT, LTE, SH_L,
            L_PAREN, R_PAREN, L_BRACKET, R_BRACKET, L_BRACE, R_BRACE,
            NUMBER, STRING,
            PLUS, MIN, MULT, DIV,
            NOT, NE,
            BIT_OR, LOGIC_OR,
            BIT_AND, LOGIC_AND,
            ASSIGN, EQ,
            COMMA, COLON,
            INSTANCEOF_KEYWORD,
            TRUE, FALSE, NULL /* "null" */, NULL /* "nil" */,
            EOF
        };

        String src = Util.readResource(TOKEN_TEST_FILE);
        TokenSource lexer = createLexer(src);

        int count = 0;
        while (true) {
            Type type = lexer.nextToken().getType();

            Assert.assertTrue(
                    "More tokens read than expected",
                    count < expectedTypes.length
            );
            Assert.assertEquals(
                    "Token mismatch",
                    expectedTypes[count], type
            );

            if (type == EOF) break;
            count++;
        }
    }

    @Test
    public void testNumericLiterals() {
        String[] literals = new String[]{"0x10", "010", "0b10", "1.0e10", "1L", "1F"};
        Class[] expectedClasses = new Class[]{
            Integer.class, Integer.class, Integer.class, Double.class, Long.class,
            Float.class
        };

        for (int i = 0; i < literals.length; i++) {
            Token token = createLexer(literals[i]).nextToken();

            Assert.assertTrue(
                    "Expected token to be a NumberToken",
                    token instanceof NumberToken
            );

            NumberToken numToken = (NumberToken) token;
            Assert.assertEquals(expectedClasses[i], numToken.getNumberType());
        }
    }

    @Test
    public void testImports() {
        String src;
        TokenSource  lex;
        Token  tok;

        // multi-class import test
        src = "import foo.bar.{baz, wut}";
        lex = createLexer(src);

        Assert.assertEquals(Type.IMPORT_KEYWORD, lex.nextToken().getType());
        tok = lex.nextToken();
        Assert.assertEquals(Type.IMPORT_PATH, tok.getType());
        Assert.assertTrue(
                "Expected token to be an ImportPathToken",
                tok instanceof ImportPathToken
        );

        // single-class import test
        src = "import foo.bar.qux";
        lex = createLexer(src);

        Assert.assertEquals(Type.IMPORT_KEYWORD, lex.nextToken().getType());
        tok = lex.nextToken();
        Assert.assertEquals(Type.IMPORT_PATH, tok.getType());
        Assert.assertTrue(
                "Expected token to be an ImportPathToken",
                tok instanceof ImportPathToken
        );
    }

    @Test
    public void testTokenPositioning() {
        String src = "ident \"string\" ++ () [] {} - + ! * / %";
        TokenSource  lex = createLexer(src);

        while (true) {
            Token tok = lex.nextToken();
            //System.out.println(tok.getType().name() + ": " + tok.getStart() + ".." + tok.getEnd());

            if (tok.getType() == Type.EOF)
                break;
        }
    }

}
