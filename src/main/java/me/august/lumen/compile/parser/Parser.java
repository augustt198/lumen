package me.august.lumen.compile.parser;

import me.august.lumen.common.Modifier;
import me.august.lumen.compile.parser.ast.*;
import me.august.lumen.compile.parser.ast.expr.*;
import me.august.lumen.compile.parser.ast.stmt.*;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import me.august.lumen.compile.scanner.Lexer;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.Type;
import me.august.lumen.compile.scanner.tokens.NumberToken;
import me.august.lumen.compile.scanner.tokens.StringToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static me.august.lumen.compile.scanner.Type.*;

public class Parser {

    private Lexer lexer;

    // for peeking tokens
    private Stack<Token> queued = new Stack<>();
    private Token current;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    /**
     * Returns the next token, without continuing to next token.
     * The maximum lookahead is 1, i.e. calling peek() twice will
     * always yield the same value.
     *
     * @return The next token
     */
    private Token peek() {
        if (queued.isEmpty()) {
            return queued.push(next());
        } else {
            return queued.get(0);
        }
    }

    /**
     * Reads the next token from the Lexer
     *
     * @return The next token
     */
    private Token next() {
        if (!queued.isEmpty()) return queued.pop();
        return current = lexer.nextToken();
    }

    /**
     * If the next token type (obtained via the peek()
     * method) is equal to the given token type, the
     * token is read and true is returned. Otherwise,
     * false is returned.
     *
     * @param tokenType The token type to accept
     * @return Whether or not the next token is of type tokenType
     */
    private boolean accept(Type tokenType) {
        if (peek().getType() == tokenType) {
            next();
            return true;
        }
        return false;
    }


    public UnresolvedType readType() {
        String ident   = next().expectType(IDENTIFIER).getContent();
        int dimensions = 0;
        while (accept(L_BRACKET)) {
            next().expectType(R_BRACKET);
            dimensions++;
        }

        return new UnresolvedType(ident, dimensions);
    }


    /**
     * The main parsing entry point.
     * Accepts the following grammar:
     * (import | class)*
     *
     * @return The program AST node
     */
    public ProgramNode parseMain() {
        List<String> imports = new ArrayList<>();

        Token token = next();

        ClassNode classNode = null;
        while (token.getType() != EOF) {
            if (token.getType() == IMPORT_KEYWORD) {
                imports.add(next().expectType(IMPORT_PATH).getContent());
            } else if (token.getType() == CLASS_KEYWORD || token.hasAttribute(Attribute.ACC_MOD)) {
                if (classNode != null) throw new RuntimeException("Previous class definition found.");

                Modifier mod = Modifier.PUBLIC;
                if (token.getType() != CLASS_KEYWORD) {
                    mod = token.getType().toModifier();
                    next().expectType(CLASS_KEYWORD); // consume `class` keyword
                }

                classNode = parseClass(mod);
            }

            token = next();
        }

        ImportNode[] importNodes = new ImportNode[imports.size()];
        for (int i = 0; i < imports.size(); i++) {
            importNodes[i] = new ImportNode(imports.get(i));
        }
        return new ProgramNode(importNodes, classNode);
    }

    /**
     * Parses a class.
     * Accepts the following grammar:
     * [superclass][interfaces](field | method)*
     *
     * @param mod The class's access modifier
     * @return THe class AST node
     */
    private ClassNode parseClass(Modifier mod) {
        String name = next().expectType(IDENTIFIER).getContent();

        Typed superClass = new Typed(parseSuperclass());
        String[] interfaces = parseInterfaces();

        next().expectType(L_BRACE); // expect {
        ClassNode cls = new ClassNode(name, superClass, interfaces, mod);

        next();
        while (current.getType() != R_BRACE) {
            parseClassFieldOrMethod(cls, current);
            next();
        }

        return cls;
    }

    /**
     * Parses either a field or method definition.
     * Accepts the following grammar:
     * (field | method)
     *
     * @param cls   The class AST node
     * @param token The current token
     */
    private void parseClassFieldOrMethod(ClassNode cls, Token token) {
        List<Modifier> modList = new ArrayList<>();
        while (token.getType().hasAttribute(Attribute.ACC_MOD)) {
            modList.add(token.getType().toModifier());
            token = next();
        }
        Modifier[] mods = modList.toArray(new Modifier[modList.size()]);

        if (token.getType() == IDENTIFIER) {
            cls.getFields().add(parseField(token, mods));
        } else if (token.getType() == DEF_KEYWORD) {
            next();
            cls.getMethods().add(parseMethod(mods));
        }
    }

    /**
     * Parses a field definition.
     * Accepts the following grammar:
     * name ":" type [= expression]
     *
     * @param token The current token
     * @param mods  The field's access modifiers
     * @return The field AST node
     */
    private FieldNode parseField(Token token, Modifier[] mods) {
        String name = token.expectType(IDENTIFIER).getContent();
        next().expectType(COLON);

        UnresolvedType type = readType();

        FieldNode field = new FieldNode(name, type, mods);

        Expression defaultValue = null;
        // field has default value
        if (accept(ASSIGN)) {
            defaultValue = parseExpression();
        }

        field.setDefaultValue(defaultValue);

        return field;
    }

    /**
     * Parses a method definition.
     * Accepts the following grammar:
     * name [":" type] "(" (name ":" type)* ")" "{" method_body "}"
     * If the type is omitted, it is assumed to be a void method
     *
     * @param mods The method's access modifiers
     * @return The method AST node
     */
    private MethodNode parseMethod(Modifier[] mods) {
        String name = current.expectType(IDENTIFIER).getContent();

        UnresolvedType type;
        if (peek().getType() == Type.COLON) {
            next(); // consume ':'
            type = readType();
        } else {
            type = UnresolvedType.VOID_TYPE;
        }

        List<Parameter> params = new ArrayList<>();

        if (peek().getType() == L_PAREN) {
            next(); // consume '('
            while (peek().getType() != R_PAREN) {
                String paramName = next().expectType(IDENTIFIER).getContent();

                next().expectType(Type.COLON);

                UnresolvedType paramType = readType();

                params.add(new Parameter(paramName, paramType));

                accept(COMMA);
            }
            next();
        }

        MethodNode method = new MethodNode(name, type, params, mods);

        if (peek().getType() == L_BRACE) {
            method.setBody(parseBody());
        }

        return method;
    }

    /**
     * Parses a body of code, within a set of braces ("{ }")
     *
     * @return The body AST node.
     */
    private Body parseBody() {
        Body body = new Body();
        next().expectType(L_BRACE);

        while (peek().getType() != R_BRACE) {
            CodeBlock code = parseStatement();
            body.addCode(code);
        }
        next();

        return body;
    }

    /**
     * Parses a statement. A statement is one
     * of the following:
     * 1. variable declaration
     * 2. a body of code
     * 3. an if-statement
     * 4. a while-loop
     * 5. a return-statement
     * 6. an expression
     *
     * Expressions are considered to be a statement
     * in this case because certain expressions can
     * act as a statement, i.e. a method call.
     * However, there are also expressions which
     * cannot act as a statement, i.e. arithmetic.
     *
     * @return A statement
     */
    private CodeBlock parseStatement() {
        if (accept(VAR_KEYWORD)) {
            return parseLocalVariable();
        } else if (accept(L_BRACE)) {
            return parseBody();
        } else if (accept(IF_KEYWORD)) {
            return parseIfStatement(false);
        } else if (accept(UNLESS_KEYWORD)) {
            return parseIfStatement(true);
        } else if (accept(WHILE_KEYWORD)) {
            return parseWhileStatement(false);
        } else if (accept(UNTIL_KEYWORD)) {
            return parseWhileStatement(true);
        } else if (accept(BREAK_KEYWORD)) {
            return new BreakStmt();
        } else if (accept(NEXT_KEYWORD)) {
            return new NextStmt();
        } else if (accept(RETURN_KEYWORD)) {
            return new ReturnStmt(parseExpression());
        } else {
            return parseExpression();
        }
    }

    /**
     * Parses a class's superclass definition
     *
     * @return The class's superclass
     */
    private UnresolvedType parseSuperclass() {
        if (accept(COLON)) {
            return readType();
        } else {
            return UnresolvedType.OBJECT_TYPE;
        }
    }

    /**
     * Parses the class's implemented interfaces
     *
     * @return The class's implemented interfaces
     */
    private String[] parseInterfaces() {
        if (accept(PLUS)) {
            List<String> interfaces = new ArrayList<>();

            next().expectType(L_PAREN);
            Token current = next();
            while (true) {
                interfaces.add(current.expectType(IDENTIFIER).getContent());

                current = next();
                if (current.getType() == COMMA) {
                    current = next();
                } else if (current.getType() == R_PAREN) {
                    break;
                }
            }

            return interfaces.toArray(new String[interfaces.size()]);
        } else {
            return new String[]{};
        }
    }

    /**
     * Parses a local variable definition.
     * Accepts the following grammar:
     * name ":" type [= expression]
     *
     * @return The variable declaration AST node
     */
    private VarStmt parseLocalVariable() {
        String name = next().expectType(IDENTIFIER).getContent();
        next().expectType(COLON);

        UnresolvedType type = readType();

        Expression value = null;
        if (accept(ASSIGN)) {
            value = parseExpression();
        }

        return new VarStmt(name, type, value);
    }

    /**
     * Parses an if-statement.
     * Accepts the following grammar:
     * "if" condition body
     * ("else if" condition body)*
     * [else body]
     *
     * @return The if-statement AST node
     */
    private IfStmt parseIfStatement(boolean inverted) {
        Expression condition = parseExpression();

        if (inverted) {
            condition = new NotExpr(condition);
        }

        Body body;
        if (accept(THEN_KEYWORD)) {
            body = new Body(Arrays.asList(parseExpression()));
            Body elseBody = null;
            if (accept(ELSE_KEYWORD)) {
                elseBody = new Body(Arrays.asList(parseExpression()));
            }
            return new IfStmt(condition, body, new ArrayList<>(), elseBody);
        }

        body = parseBody();

        List<IfStmt.ElseIf> elseIfs = new ArrayList<>();
        Body elseBody = null;

        while (accept(ELSE_KEYWORD)) {
            if (accept(IF_KEYWORD)) {
                IfStmt.ElseIf elseIf = new IfStmt.ElseIf(
                    parseExpression(), parseBody()
                );
                elseIfs.add(elseIf);
            } else { // so meta
                if (elseBody != null) throw new RuntimeException("There is already an else branch");
                elseBody = parseBody();
            }
        }

        return new IfStmt(condition, body, elseIfs, elseBody);
    }

    /**
     * Parses a while-statement.
     * Accepts the following grammar:
     * "while" condition body
     *
     * @return The while-statement AST node
     */
    private WhileStmt parseWhileStatement(boolean inverted) {
        Expression condition = parseExpression();

        if (inverted) {
            condition = new NotExpr(condition);
        }

        Body body = parseBody();

        return new WhileStmt(condition, body);
    }

    /**
     * The expression parsing entry point.
     *
     * @return An expression
     */
    public Expression parseExpression() {
        return parseAssignment();
    }

    /**
     * Parses an assignment expression:
     * ternary [= expression]
     *
     * @return An expression
     */
    private Expression parseAssignment() {
        Expression left = parseTernary();

        if (accept(ASSIGN)) {
            if (!(left instanceof IdentExpr))
                throw new RuntimeException("Left hand expression must be an identifier");
            Expression right = parseExpression();

            return new AssignmentExpr((IdentExpr) left, right);
        }

        return left;
    }

    /**
     * Parses a ternary expression:
     * logic_or [? expression : expression]
     *
     * @return An expression
     */
    private Expression parseTernary() {
        Expression condition = parseRange();

        if (accept(QUESTION)) {
            Expression trueExpr = parseExpression();
            next().expectType(Type.COLON);
            Expression falseExpr = parseExpression();

            return new TernaryExpr(condition, trueExpr, falseExpr);
        }

        return condition;
    }

    private Expression parseRange() {
        Expression left = parseLogicOr();

        if (accept(RANGE)) {
            if (!(left instanceof IdentExpr))
                throw new RuntimeException("Expected identifier");
            IdentExpr ident = (IdentExpr) left;
            UnresolvedType type = new UnresolvedType(ident.getIdentifier());

            List<Expression> lengths = new ArrayList<>();
            int dims = 1;

            next().expectType(L_BRACKET);
            lengths.add(parseExpression());
            next().expectType(R_BRACKET);

            boolean unknown = false;
            while (accept(L_BRACKET)) {
                if (accept(QUESTION)) {
                    unknown = true;
                    dims++;
                } else {
                    if (unknown)
                        throw new RuntimeException("Illegal array initialization");
                    lengths.add(parseExpression());
                    dims++;
                }
                next().expectType(R_BRACKET);
            }

            return new ArrayInitializerExpr(type, lengths, dims);
        }

        return left;
    }

    /**
     * Parses a logical OR expression:
     * logic_and [|| expression]
     *
     * @return An expression
     */
    private Expression parseLogicOr() {
        Expression left = parseLogicAnd();

        if (accept(LOGIC_OR)) {
            Expression right = parseExpression();
            return new OrExpr(left, right);
        }

        return left;
    }

    /**
     * Parses a logical AND expression:
     * bitwise_or [&& expression]
     *
     * @return An expression
     */
    private Expression parseLogicAnd() {
        Expression left = parseBitOr();

        if (accept(LOGIC_AND)) {
            Expression right = parseExpression();
            return new AndExpr(left, right);
        }

        return left;
    }

    /**
     * Parses a bitwise OR expression:
     * bitwise_xor [| expression]
     *
     * @return An expression
     */
    private Expression parseBitOr() {
        Expression left = parseBitXor();

        if (accept(BIT_OR)) {
            Expression right = parseExpression();
            return new BitOrExpr(left, right);
        }

        return left;
    }

    /**
     * Parses a bitwise XOR expression:
     * bitwise_and [^ expression]
     *
     * @return An expression
     */
    private Expression parseBitXor() {
        Expression left = parseBitAnd();

        if (accept(BIT_XOR)) {
            Expression right = parseExpression();
            return new BitXorExpr(left, right);
        }

        return left;
    }

    /**
     * Parses a bitwise AND expression:
     * equality [& expression]
     *
     * @return An expression
     */
    private Expression parseBitAnd() {
        Expression left = parseEq();

        if (accept(BIT_AND)) {
            Expression right = parseExpression();
            return new BitAndExpr(left, right);
        }

        return left;
    }

    /**
     * Parses an equality expression:
     * relational [(eq | ne) expression]
     *
     * @return An expression
     */
    private Expression parseEq() {
        Expression left = parseRelational();

        Type peek = peek().getType();
        if (peek == Type.EQ || peek == Type.NE) {
            next(); // consume EQ or NE token

            Expression right = parseExpression();
            EqExpr.Op op = peek == Type.EQ ? EqExpr.Op.EQ : EqExpr.Op.NE;
            return new EqExpr(left, right, op);
        }

        return left;
    }

    /**
     * Parses a relational expression:
     * bit_shift [(lt | lte | gt | gte | is a) expression]
     *
     * @return An expression
     */
    private Expression parseRelational() {
        Expression left = parseShift();

        Type peek = peek().getType();
        if (peek == LT || peek == GT || peek == LTE || peek == GTE ||
            peek == INSTANCEOF_KEYWORD || peek == NOT_INSTANCEOF_KEYWORD) {
            next(); // consume

            if (peek == INSTANCEOF_KEYWORD) {
                UnresolvedType type = readType();
                return new InstanceofExpr(left, type);
            } else if (peek == NOT_INSTANCEOF_KEYWORD) {
                UnresolvedType type = readType();
                return new NotExpr(new InstanceofExpr(left, type));
            } else {
                RelExpr.Op op = RelExpr.Op.valueOf(peek.name());
                Expression right = parseExpression();
                return new RelExpr(left, right, op);
            }

        }

        return left;
    }

    /**
     * Parses a bit shift expression:
     * additive [(sh_l | sh_r | u_sh_r) expression]
     *
     * @return An expression
     */
    private Expression parseShift() {
        Expression left = parseAdditive();

        Type peek = peek().getType();
        if (peek == Type.SH_L || peek == Type.SH_R || peek == Type.U_SH_R) {
            next();
            Expression right = parseExpression();
            ShiftExpr.Op op = ShiftExpr.Op.valueOf(peek.name());

            return new ShiftExpr(left, right, op);
        }

        return left;
    }

    /**
     * Parses an additive expression:
     * multiplicative [(+ | -) expression]
     *
     * @return An expression
     */
    private Expression parseAdditive() {
        Expression left = parseMultiplicative();

        Type peek = peek().getType();
        if (peek == Type.PLUS || peek == Type.MIN) {
            next();
            Expression right = parseExpression();
            AddExpr.Op op = peek == Type.PLUS ? AddExpr.Op.ADD : AddExpr.Op.SUB;

            return new AddExpr(left, right, op);
        }

        return left;
    }

    /**
     * Parses a multiplicative expression:
     * cast [(* | /) expression]
     *
     * @return An expression
     */
    private Expression parseMultiplicative() {
        Expression left = parseCast();

        Type peek = peek().getType();
        if (peek == Type.MULT || peek == Type.DIV || peek == Type.REM) {
            next();
            Expression right = parseExpression();
            MultExpr.Op op = MultExpr.Op.valueOf(peek.name());

            return new MultExpr(left, right, op);
        }

        return left;
    }

    /**
     * Parses a casting expression:
     * unary [as identifier]
     *
     * @return An expression
     */
    private Expression parseCast() {
        Expression left = parseUnary();

        if (accept(CAST_KEYWORD)) {
            UnresolvedType type = readType();

            return new CastExpr(left, type);
        }

        return left;
    }

    /**
     * Parses a unary expression:
     * [+ - ++ --] component
     *
     * @return An expression
     */
    private Expression parseUnary() {
        if (accept(MIN)) {
            return new UnaryMinusExpr(parsePostfix());
        } else if (accept(PLUS)) {
            // unary plus does nothing important at the moment
            return parsePostfix();
        } else if (accept(BIT_COMP)) { // bitwise complement
            return new BitwiseComplementExpr(parsePostfix());
        } else if (accept(NOT)) {
            return new NotExpr(parsePostfix());
        } else if (accept(INC)) {
            return new IncrementExpr(parsePostfix(), IncrementExpr.Op.INC, false);
        } else if (accept(DEC)) {
            return new IncrementExpr(parsePostfix(), IncrementExpr.Op.DEC, false);
        } else {
            return parsePostfix();
        }
    }

    private Expression parsePostfix() {
        Expression expr = parseComponent();
        if (accept(INC)) {
            return new IncrementExpr(expr, IncrementExpr.Op.INC, true);
        } else if (accept(DEC)) {
            return new IncrementExpr(expr, IncrementExpr.Op.DEC, true);
        } else {
            while (accept(L_BRACKET)) {
                Expression index = parseExpression();
                next().expectType(R_BRACKET);

                expr = new ArrayAccessExpr(expr, index);
            }
            return expr;
        }
    }

    /**
     * Parses an expression component: string, number,
     * true, false, null, grouping, identifier, method
     *
     * @return An expression component
     */
    private Expression parseComponent() {
        Token token = peek();

        Expression expr;
        if (token instanceof StringToken) {
            next();
            // set expression to a string literal
            expr =  new StringExpr(token.getContent(), ((StringToken) token).getQuoteType());
        } else if (token.getType() == Type.NUMBER) {
            next();
            // set expression to a numeric literal
            NumberToken numToken = (NumberToken) token;
            expr = new NumExpr(numToken.getValue());
        } else if (token.getType() == Type.TRUE) {
            next();
            // set expression to `true`
            expr = new TrueExpr();
        } else if (token.getType() == Type.FALSE) {
            next();
            // set expression to `false`
            expr =  new FalseExpr();
        } else if (token.getType() == Type.NULL) {
            next();
            // set expression to `null`
            expr = new NullExpr();
        } else if (token.getType() == Type.L_PAREN) {
            // parse a grouped expression within parens
            next();
            expr = parseExpression();
            next().expectType(R_PAREN);
        } else if (token.getType() == Type.IDENTIFIER) {
            next();
            String ident = token.getContent();

            // separated with '::'
            if (accept(SEP)) {
                String memberName = next().expectType(Type.IDENTIFIER).getContent();

                // create a either a static field or static method call
                if (accept(L_PAREN)) {
                    List<Expression> params = parseExpressionList();

                    UnresolvedType type = new UnresolvedType(ident);
                    expr = new StaticMethodCall(type, memberName, params);
                } else {
                    UnresolvedType type = new UnresolvedType(ident);
                    expr = new StaticField(type, memberName);
                }
            // parse as a normal identifier or method
            } else {
                expr = identOrMethod(ident);
            }
        } else {
            throw new RuntimeException("Unexpected token: " + token);
        }

        while (accept(DOT)) {
            expr = parseMember(expr);
        }

        return expr;
    }

    /**
     * Parses a member of the given expression (owner).
     * A member is either a field or method call.
     * @param owner The given expression
     * @return A method call or identifier expression belonging to `owner`
     */
    private Expression parseMember(Expression owner) {
        String ident = next().expectType(Type.IDENTIFIER).getContent();

        if (accept(L_PAREN)) {
            List<Expression> params = parseExpressionList();
            return new MethodCallExpr(ident, params, owner);
        } else {
            return new IdentExpr(ident, owner);
        }
    }

    /**
     * Gets an identifier expression, or method call
     * expression depending on the syntax used. Assumes
     * the IDENTIFIER token has already been read. If
     * the next token is a left paren, a MethodCallExpr
     * is returned. Otherwise, an IdentExpr is returned.
     *
     * @param ident The identifier token content
     * @return A method call or identifier expression
     */
    private Expression identOrMethod(String ident) {
        if (accept(L_PAREN)) {
            List<Expression> params = parseExpressionList();
            return new MethodCallExpr(ident, params);
        } else {
            return new IdentExpr(ident);
        }
    }

    private List<Expression> parseExpressionList() {
        return parseExpressionList(COMMA, R_PAREN);
    }

    /**
     * Parses a list of expressions with a given delimiter
     * and ending token type
     *
     * @param delim The delimiter token type
     * @param end   The ending token type
     * @return A list of expressions
     */
    private List<Expression> parseExpressionList(Type delim, Type end) {
        List<Expression> exprs = new ArrayList<>();

        while (peek().getType() != end) {
            exprs.add(parseExpression());
            accept(delim);
        }
        next().expectType(end);

        return exprs;
    }

}
