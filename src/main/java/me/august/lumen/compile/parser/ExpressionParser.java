package me.august.lumen.compile.parser;

import me.august.lumen.compile.CompileBuildContext;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.components.*;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.TokenSource;
import me.august.lumen.compile.scanner.Type;
import me.august.lumen.compile.scanner.pos.Span;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static me.august.lumen.compile.scanner.Type.*;

public class ExpressionParser implements TokenParser {

    private final static Map<Type, PrefixParser> prefixParsers  = new HashMap<>();
    private final static Map<Type, InfixParser>  infixParsers   = new HashMap<>();
    private final static Map<Type, InfixParser>  postfixParsers = new HashMap<>();

    static {
        UnaryPrefixParser unaryPrefixParser = new UnaryPrefixParser();
        prefixParsers.put(PLUS,     unaryPrefixParser);
        prefixParsers.put(MIN,      unaryPrefixParser);
        prefixParsers.put(BIT_COMP, unaryPrefixParser);
        prefixParsers.put(INC,      unaryPrefixParser);
        prefixParsers.put(DEC,      unaryPrefixParser);
        prefixParsers.put(NOT,      unaryPrefixParser);
        prefixParsers.put(IDENTIFIER, new IdentifierParser());
        prefixParsers.put(NUMBER,     ComponentParsers.NUMBER_PARSER);
        prefixParsers.put(L_PAREN,    ComponentParsers.GROUPING_PARSER);
        prefixParsers.put(STRING,     ComponentParsers.STRING_PARSER);
        prefixParsers.put(TRUE,       ComponentParsers.BOOLEAN_PARSER);
        prefixParsers.put(FALSE,      ComponentParsers.BOOLEAN_PARSER);
        prefixParsers.put(NULL,       ComponentParsers.NULL_PARSER);


        infixParsers.put(ASSIGN, new AssignmentParser());

        // infix, prefix, and mixfix parsers
        AdditiveParser additiveParser = new AdditiveParser();
        infixParsers.put(PLUS, additiveParser);
        infixParsers.put(MIN,  additiveParser);

        MultiplicativeParser multiplicativeParser = new MultiplicativeParser();
        infixParsers.put(MULT, multiplicativeParser);
        infixParsers.put(DIV, multiplicativeParser);
        infixParsers.put(REM, multiplicativeParser);

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

        RangeParser rangeParser = new RangeParser();
        infixParsers.put(RANGE_INCLUSIVE, rangeParser);
        infixParsers.put(RANGE_EXCLUSIVE, rangeParser);

        PostfixParser postfixParser = new PostfixParser();
        postfixParsers.put(INC,       postfixParser);
        postfixParsers.put(DEC,       postfixParser);
        postfixParsers.put(L_BRACKET, postfixParser);
    }

    private TokenSource tokenSource;
    private Stack<Token> queuedTokens = new Stack<>();

    private Stack<Span> spanRecorder = new Stack<>();

    private BuildContext buildContext;

    public ExpressionParser(TokenSource tokenSource) {
        this.tokenSource  = tokenSource;
        this.buildContext = new CompileBuildContext();
    }


    public ExpressionParser(TokenSource tokenSource, BuildContext buildContext) {
        this.tokenSource  = tokenSource;
        this.buildContext = buildContext;
    }

    @Override
    public Token consume() {
        Token token;
        if (!queuedTokens.empty()) {
            token = queuedTokens.pop();
        } else {
            token = tokenSource.nextToken();
        }

        for (Span span : spanRecorder) {
            if (span.getStart() < 0) {
                span.setStart(token.getStart());
            }

            span.setEnd(token.getEnd());
        }

        return token;
    }

    @Override
    public Token peek() {
        if (!queuedTokens.empty()) {
            return queuedTokens.get(0);
        } else {
            return queuedTokens.push(tokenSource.nextToken());
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

    protected void startRecording() {
        spanRecorder.push(new Span(-1, 0));
    }

    protected void stopRecording() {
        spanRecorder.pop();
    }

    protected <T> T endRecording(T obj) {
        Span span = spanRecorder.pop();
        buildContext.positionMap().put(obj, span);
        return obj;
    }

    protected <T> T keepAndEndRecording(T obj) {
        Span spanCopy = new Span(spanRecorder.lastElement());
        buildContext.positionMap().put(obj, spanCopy);
        return obj;
    }

    @Override
    public Expression parseExpression(int predence) {
        startRecording();
        Token token = consume();

        PrefixParser prefixParser = prefixParsers.get(token.getType());

        if (prefixParser == null) {
            throw new RuntimeException("Unexpected token: " + token);
        }

        Expression left = prefixParser.parse(this, token);
        keepAndEndRecording(left);

        while (postfixParsers.containsKey(peek().getType())) {
            token = consume();
            left = postfixParsers.get(token.getType()).parse(this, left, token);
            keepAndEndRecording(left);
        }

        while (predence < getPrecedence()) {
            token = consume();

            InfixParser infix = infixParsers.get(token.getType());
            left = infix.parse(this, left, token);
            keepAndEndRecording(left);
        }

        stopRecording();
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

    public BuildContext getBuildContext() {
        return buildContext;
    }
}
