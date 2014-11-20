package me.august.lumen.compile.parser;

import me.august.lumen.common.Modifier;
import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.FieldNode;
import me.august.lumen.compile.parser.ast.ImportNode;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.parser.ast.expr.*;
import me.august.lumen.compile.scanner.Lexer;
import me.august.lumen.compile.scanner.Op;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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

    // maximum lookhead = 1
    private Token peek() {
        if (queued.isEmpty()) {
            return queued.push(next());
        } else {
            return queued.get(0);
        }
    }

    private Token next() {
        if (!queued.isEmpty()) return queued.pop();
        return current = lexer.nextToken();
    }

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

    private ClassNode parseClass(Modifier mod) {
        String name = next().expectType(IDENTIFIER).getContent();
        Token token = next();

        String superClass   = parseSuperclass(token);
        token = next();
        String[] interfaces = parseInterfaces(token);

        next().expectType(L_BRACE); // expect {
        ClassNode cls = new ClassNode(name, superClass, interfaces, mod);

        next();
        while (current.getType() != R_BRACE) {
            parseClassFieldOrMethod(cls, current);
            next();
        }

        return cls;
    }

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
            // TODO parse methods
        }
    }

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

    private String parseSuperclass(Token token) {
        if (token.getType() == COLON) {
            return next().expectType(IDENTIFIER, "Expected superclass identifier").getContent();
        } else {
            return OBJECT_CLASS_NAME;
        }
    }

    private String[] parseInterfaces(Token token) {
        if (token.getType() == PLUS) {
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

    public Expression parseExpression() {
        Expression left = parseLogicAnd();

        if (peek().getType() == Type.LOGIC_OR) {
            next(); // consume LOGIC_OR token

            Expression right = parseExpression();
            return new OrExpr(left, right);
        }

        return left;
    }

    private Expression parseLogicAnd() {
        Expression left = parseBitOr();

        if (peek().getType() == Type.LOGIC_AND) {
            next(); // consume LOGIC_OR token

            Expression right = parseExpression();
            return new AndExpr(left, right);
        }

        return left;
    }

    private Expression parseBitOr() {
        Expression left = parseBitXor();

        if (peek().getType() == Type.BIT_OR) {
            next(); // consume BIT_OR token

            Expression right = parseExpression();
            return new BitOrExpr(left, right);
        }

        return left;
    }

    private Expression parseBitXor() {
        Expression left = parseBitAnd();

        if (peek().getType() == Type.BIT_XOR) {
            next(); // consume BIT_XOR token

            Expression right = parseExpression();
            return new BitXorExpr(left, right);
        }

        return left;
    }

    private Expression parseBitAnd() {
        Expression left = parseEq();

        if (peek().getType() == Type.BIT_AND) {
            next(); // consume BIT_AND token

            Expression right = parseExpression();
            return new BitAndExpr(left, right);
        }

        return left;
    }

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

    private Expression parseRelational() {
        Expression left = parseShift();

        Type peek = peek().getType();
        if (peek == Type.LT || peek == Type.GT || peek == Type.LTE ||
            peek == Type.GTE || peek == Type.IS_KEYWORD) {
            next(); // consume

            Expression right = parseExpression();
            Op op;
            switch (peek) {
                case LT: op = Op.LT; break;
                case GT: op = Op.GT; break;
                case LTE: op = Op.LTE; break;
                case GTE: op = Op.GTE; break;
                case IS_KEYWORD: op = Op.IS; break;
                default: op = null;
            }

            return new RelExpr(left, right, op);
        }

        return left;
    }

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

    private Expression parseComponent() {
        Token token = peek();

        if (token.getType() == Type.STRING) {
            next();
            return new StringExpr(token.getContent());
        } else if (token.getType() == Type.NUMBER) {
            next();
            return new NumExpr(Double.parseDouble(token.getContent()));
        } else if (token.getType() == Type.L_PAREN) {
            next();
            Expression expr = parseExpression();
            next().expectType(R_PAREN);

            return expr;
        } else if (token.getType() == Type.IDENTIFIER) {
            next();
            return new IdentExpr(token.getContent());
        } else {
            throw new RuntimeException("Unexpected token: " + token);
        }
    }

}
