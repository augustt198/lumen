package me.august.lumen.compile.parser;

import me.august.lumen.common.Modifier;
import me.august.lumen.compile.parser.ast.*;
import me.august.lumen.compile.parser.ast.stmt.Body;
import me.august.lumen.compile.parser.ast.stmt.IfStmt;
import me.august.lumen.compile.parser.ast.stmt.VarStmt;
import me.august.lumen.compile.parser.ast.stmt.WhileStmt;
import me.august.lumen.compile.parser.ast.expr.*;
import me.august.lumen.compile.parser.ast.expr.TernaryExpr;
import me.august.lumen.compile.parser.ast.expr.owned.ClassExpr;
import me.august.lumen.compile.parser.ast.expr.owned.OwnedExpr;
import me.august.lumen.compile.parser.ast.expr.owned.OwnedField;
import me.august.lumen.compile.parser.ast.expr.owned.OwnedMethod;
import me.august.lumen.compile.scanner.Lexer;
import me.august.lumen.compile.scanner.Op;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.Type;

import java.util.*;

import static me.august.lumen.compile.scanner.Type.*;

public class Parser {

    private static final String OBJECT_CLASS_NAME = Object.class.getName();

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

        String superClass = parseSuperclass();
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
        String type = next().expectType(IDENTIFIER).getContent();

        FieldNode field = new FieldNode(name, type, mods);

        Expression defaultValue = null;
        // field has default value
        if (peek().getType() == Type.ASSIGN) {
            next(); // consume

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
        next(); // consume name

        String type;
        if (current.getType() == Type.COLON) {
            type = next().expectType(IDENTIFIER).getContent();
            next(); // consume type
        } else {
            type = "void";
        }

        current.expectType(L_PAREN);
        next(); // consume ')'

        Map<String, String> params = new HashMap<>();

        while (current.getType() != R_PAREN) {
            String paramName = current.expectType(IDENTIFIER).getContent();

            next().expectType(Type.COLON);

            String paramType = next().expectType(IDENTIFIER).getContent();
            next(); // consume param type

            params.put(paramName, paramType);
        }

        MethodNode method = new MethodNode(name, type, mods);
        method.setParameters(params);

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
            Type ty = peek().getType();
            CodeBlock code;
            if (ty == VAR_KEYWORD) {
                next();
                code = parseLocalVariable();
            } else if (ty == L_BRACE) {
                code = parseBody();
            } else if (ty == IF_KEYWORD) {
                next();
                code = parseIfStatement();
            } else if (ty == WHILE_KEYWORD) {
                next();
                code = parseWhileStatement();
            } else {
                code = parseExpression();
            }
            body.addCode(code);
        }
        next();

        return body;
    }

    /**
     * Parses a class's superclass definition
     *
     * @return The class's superclass
     */
    private String parseSuperclass() {
        if (peek().getType() == COLON) {
            next();
            return next().expectType(IDENTIFIER, "Expected superclass identifier").getContent();
        } else {
            return OBJECT_CLASS_NAME;
        }
    }

    /**
     * Parses the class's implemented interfaces
     *
     * @return The class's implemented interfaces
     */
    private String[] parseInterfaces() {
        if (peek().getType() == PLUS) {
            next();
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
        String type = next().expectType(IDENTIFIER).getContent();

        Expression value = null;
        if (peek().getType() == ASSIGN) {
            next(); // consume '='
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
    private IfStmt parseIfStatement() {
        Expression condition = parseExpression();
        Body body = parseBody();

        List<IfStmt.ElseIf> elseIfs = new ArrayList<>();
        Body elseBody = null;

        while (peek().getType() == ELSE_KEYWORD) {
            next(); // consume 'else'
            if (peek().getType() == IF_KEYWORD) {
                next(); // consume 'if'
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
    private WhileStmt parseWhileStatement() {
        Expression condition = parseExpression();
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

        if (peek().getType() == Type.ASSIGN) {
            next(); // consume '='
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
        Expression condition = parseLogicOr();

        if (peek().getType() == Type.QUESTION) {
            next(); // consume '?'

            Expression trueExpr = parseExpression();
            next().expectType(Type.COLON);
            Expression falseExpr = parseExpression();

            return new TernaryExpr(condition, trueExpr, falseExpr);
        }

        return condition;
    }

    /**
     * Parses a logical OR expression:
     * logic_and [|| expression]
     *
     * @return An expression
     */
    private Expression parseLogicOr() {
        Expression left = parseLogicAnd();

        if (peek().getType() == Type.LOGIC_OR) {
            next(); // consume LOGIC_OR token

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

        if (peek().getType() == Type.LOGIC_AND) {
            next(); // consume LOGIC_OR token

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

        if (peek().getType() == Type.BIT_OR) {
            next(); // consume BIT_OR token

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

        if (peek().getType() == Type.BIT_XOR) {
            next(); // consume BIT_XOR token

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

        if (peek().getType() == Type.BIT_AND) {
            next(); // consume BIT_AND token

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
            return new EqExpr(left, right, peek == Type.EQ ? Op.EQ : Op.NE);
        }

        return left;
    }

    /**
     * Parses a relational expression:
     * bit_shift [(lt | lte | gt | gte | is) expression]
     *
     * @return An expression
     */
    private Expression parseRelational() {
        Expression left = parseShift();

        Type peek = peek().getType();
        if (peek == Type.LT || peek == Type.GT || peek == Type.LTE ||
            peek == Type.GTE || peek == Type.IS_KEYWORD) {
            next(); // consume

            Expression right = parseExpression();
            Op op = peek == IS_KEYWORD ? Op.IS : Op.valueOf(peek.name());

            return new RelExpr(left, right, op);
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
            Op op = Op.valueOf(peek.name());

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
            Op op = peek == Type.PLUS ? Op.ADD : Op.SUB;

            return new AddExpr(left, right, op);
        }

        return left;
    }

    /**
     * Parses a multiplicative expression:
     * component [(* | /) expression]
     *
     * @return An expression
     */
    private Expression parseMultiplicative() {
        Expression left = parseComponent();

        Type peek = peek().getType();
        if (peek == Type.MULT || peek == Type.DIV) {
            next();
            Expression right = parseExpression();
            Op op = peek == Type.MULT ? Op.MUL : Op.DIV;

            return new MultExpr(left, right, op);
        }

        return left;
    }

    /**
     * Parses an expression component: string, number,
     * true, false, null, grouping, identifier, method
     *
     * @return An expression component
     */
    private Expression parseComponent() {
        Token token = peek();

        if (token.getType() == Type.STRING) {
            next();
            return new StringExpr(token.getContent());
        } else if (token.getType() == Type.NUMBER) {
            next();
            return new NumExpr(Double.parseDouble(token.getContent()));
        } else if (token.getType() == Type.TRUE) {
            next();
            return new TrueExpr();
        } else if (token.getType() == Type.FALSE) {
            next();
            return new FalseExpr();
        } else if (token.getType() == Type.NULL) {
            next();
            return new NullExpr();
        } else if (token.getType() == Type.L_PAREN) {
            next();
            Expression expr = parseExpression();
            next().expectType(R_PAREN);
            return expr;
        } else if (token.getType() == Type.IDENTIFIER) {
            return parseIdentSequence(null);
        } else {
            throw new RuntimeException("Unexpected token: " + token);
        }
    }


    /**
     * Parses a sequence of connected identifiers.
     * Examples:
     * foo.bar.qux
     * foo().bar().qux
     * foo::bar().qux
     *
     * @param owner The sequence's last owner
     * @return Parsed expression
     */
    private Expression parseIdentSequence(OwnedExpr owner) {
        String str = next().expectType(Type.IDENTIFIER).getContent();

        Type peek = peek().getType();

        OwnedExpr currentExpr;
        if (peek == Type.L_PAREN) {
            next(); // consume '('
            List<Expression> params = parseExpressionList(Type.COMMA, Type.R_PAREN);
            currentExpr = new OwnedMethod(str, params, owner);
        } else {
            currentExpr = owner == null ? new IdentExpr(str) : new OwnedField(str, owner);
        }

        peek = peek().getType();
        if (peek == Type.DOT) {
            next(); // consume '.'

            return parseIdentSequence(currentExpr);
        } else if (peek == Type.SEP) {
            next(); // consume '::'

            if (currentExpr instanceof OwnedMethod) throw new RuntimeException("Tried to use separator on method");
            return parseIdentSequence(new ClassExpr(str));
        }
        return currentExpr;
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
            if (peek().getType() == delim) {
                next();
            }
        }
        next().expectType(end);

        return exprs;
    }

}
