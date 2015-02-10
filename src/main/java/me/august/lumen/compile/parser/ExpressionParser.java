package me.august.lumen.compile.parser;

import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.components.*;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.TokenSource;
import me.august.lumen.compile.scanner.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static me.august.lumen.compile.scanner.Type.*;

public class ExpressionParser implements TokenParser {

    private final static Map<Type, PrefixParser> prefixParsers = new HashMap<>();
    private final static Map<Type, InfixParser>  infixParsers  = new HashMap<>();

    static {
        UnaryPrefixParser unaryPrefixParser = new UnaryPrefixParser();
        prefixParsers.put(PLUS,     unaryPrefixParser);
        prefixParsers.put(MIN,      unaryPrefixParser);
        prefixParsers.put(BIT_COMP, unaryPrefixParser);
        prefixParsers.put(INC,      unaryPrefixParser);
        prefixParsers.put(DEC,      unaryPrefixParser);
        prefixParsers.put(NOT,      unaryPrefixParser);
        prefixParsers.put(IDENTIFIER, ComponentParsers.IDENTIFIER_PARSER);
        prefixParsers.put(NUMBER,     ComponentParsers.NUMBER_PARSER);
        prefixParsers.put(L_PAREN,    ComponentParsers.GROUPING_PARSER);


        infixParsers.put(ASSIGN, new AssignmentParser());

        // infix, prefix, and mixfix parsers
        AdditiveParser additiveParser = new AdditiveParser();
        infixParsers.put(PLUS, additiveParser);
        infixParsers.put(MIN,  additiveParser);

        MultiplicativeParser multiplicativeParser = new MultiplicativeParser();
        infixParsers.put(MULT, multiplicativeParser);
        infixParsers.put(DIV, multiplicativeParser);
        infixParsers.put(REM, multiplicativeParser);

        PostfixParser postfixParser = new PostfixParser();
        infixParsers.put(INC, postfixParser);
        infixParsers.put(DEC, postfixParser);

        infixParsers.put(QUESTION, new TernaryParser());
        infixParsers.put(RESCUE_KEYWORD, new RescueParser());

        EqualityParser equalityParser = new EqualityParser();
        infixParsers.put(EQ, equalityParser);
        infixParsers.put(NE, equalityParser);

        RelationalParser relParser = new RelationalParser();
        infixParsers.put(GT, relParser);
        infixParsers.put(GTE, relParser);
        infixParsers.put(LT, relParser);
        infixParsers.put(LTE, relParser);
        infixParsers.put(INSTANCEOF_KEYWORD, relParser);
        infixParsers.put(NOT_INSTANCEOF_KEYWORD, relParser);

        ShiftParser shiftParser = new ShiftParser();
        infixParsers.put(SH_L, shiftParser);
        infixParsers.put(SH_R, shiftParser);
        infixParsers.put(U_SH_R, shiftParser);

        infixParsers.put(CAST_KEYWORD, new CastParser());
    }

    private TokenSource tokenSource;
    private Stack<Token> queuedTokens = new Stack<>();

    @Override
    public Token consume() {
        if (!queuedTokens.empty()) {
            return queuedTokens.pop();
        }
        return tokenSource.nextToken();
    }

    protected Token peek() {
        if (!queuedTokens.empty()) {
            return queuedTokens.get(0);
        } else {
            return queuedTokens.push(consume());
        }
    }

    @Override
    public boolean expect(Type type) {
        Token token = consume();
        if (token.getType() != type) {
            throw new RuntimeException("Expected " + type + ", found " + token.getType());
        }

        return true;
    }

    @Override
    public boolean accept(Type type) {
        if (peek().getType() == type) {
            consume();
            return true;
        }

        return false;
    }

    public ExpressionParser(TokenSource tokenSource) {
        this.tokenSource = tokenSource;
    }

    @Override
    public Expression parseExpression(int predence) {
        Token token = consume();

        PrefixParser prefixParser = prefixParsers.get(token.getType());

        if (prefixParser == null) {
            throw new RuntimeException("Unexpected token: " + token);
        }

        Expression left = prefixParser.parse(this, token);

        while (predence < getPrecedence()) {
            token = consume();

            InfixParser infix = infixParsers.get(token.getType());
            left = infix.parse(this, left, token);
        }

        return left;
    }

    private int getPrecedence() {
        InfixParser parser = infixParsers.get(peek().getType());
        if (parser != null) {
            return parser.getPrecedence();
        } else {
            return 0;
        }
    }

}
