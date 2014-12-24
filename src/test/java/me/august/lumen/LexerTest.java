package me.august.lumen;

import me.august.lumen.compile.scanner.Lexer;
import me.august.lumen.compile.scanner.Type;
import org.junit.Assert;
import org.junit.Test;

import static me.august.lumen.compile.scanner.Type.*;

public class LexerTest {

    private static final String TOKEN_TEST_FILE = "/token_test.txt";

    @Test
    public void testTokens() {
        Type[] expectedTypes = {
            IDENTIFIER,
            GT, GTE, SH_R, U_SH_R,
            LT, LTE, SH_L,
            L_PAREN, R_PAREN, L_BRACKET, R_BRACKET, L_BRACE, R_BRACE,
            NUMBER, STRING,
            PLUS, MIN, MULT, DIV,
            NEG, NE,
            BIT_OR, LOGIC_OR,
            BIT_AND, LOGIC_AND,
            ASSIGN, EQ,
            COMMA, COLON,
            TRUE, FALSE, NULL /* "null" */, NULL /* "nil" */,
            EOF
        };

        String src = Util.readResource(TOKEN_TEST_FILE);
        Lexer lexer = new Lexer(src);

        int count = 0;
        while (true) {
            Type type = lexer.nextToken().getType();

            Assert.assertTrue("More tokens read than expected", count < expectedTypes.length);
            Assert.assertEquals("Token mismatch", expectedTypes[count], type);

            if (type == EOF) break;
            count++;
        }
    }

    @Test
    public void testNumericLiterals() {
        for (String src : new String[]{"0x10", "010", "0b10"}) {
            new Lexer(src).nextToken();
        }
    }

}
