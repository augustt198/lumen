package me.august.lumen.compile.parser;

import me.august.lumen.common.ModifierSet;
import me.august.lumen.compile.parser.ast.*;
import me.august.lumen.compile.parser.ast.expr.Expression;
import me.august.lumen.compile.parser.ast.expr.NotExpr;
import me.august.lumen.compile.parser.ast.expr.RangeExpr;
import me.august.lumen.compile.parser.ast.stmt.*;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.TokenSource;
import me.august.lumen.compile.scanner.tokens.ImportPathToken;

import java.util.ArrayList;
import java.util.List;

import static me.august.lumen.compile.scanner.Type.*;

public class LumenParser extends ExpressionParser {

    public LumenParser(TokenSource tokenSource) {
        super(tokenSource);
    }

    private ModifierSet parseModifiers() {
        ModifierSet modifiers = new ModifierSet();

        while (peek().isModifier()) {
            modifiers.add(consume().getType().toModifier());
        }

        return modifiers;
    }


    public ProgramNode parseProgram() {
        List<ImportNode> imports    = new ArrayList<>();
        Token token                 = consume();
        ClassNode classNode         = null;

        while (token.getType() != EOF) {
            // Handle import declaration
            if (token.getType() == IMPORT_KEYWORD) {

                ImportPathToken pathToken = (ImportPathToken) token;
                imports.add(new ImportNode(
                        pathToken.getPath(), pathToken.getClasses()
                ));

            // Handle class declaration
            } else if (token.getType() == CLASS_KEYWORD || token.isModifier()) {
                ModifierSet modifiers = new ModifierSet();

                if (token.isModifier()) {
                    modifiers.add(token.getType().toModifier());
                    modifiers.merge(parseModifiers());
                    expect(CLASS_KEYWORD);
                }

                classNode = parseClass(modifiers);
            }

            token = consume();
        }

        return new ProgramNode(imports, classNode);
    }

    private ClassNode parseClass(ModifierSet modifiers) {
        String name = consume().expectType(IDENTIFIER).getContent();

        Typed superClass = new Typed(parseSuperclass());
        String[] interfaces = parseImplementsInterfaces();

        expect(L_BRACE);

        ClassNode classNode = new ClassNode(name, superClass, interfaces, modifiers);

        while (!accept(R_BRACE)) {
            parseMethodOrField(classNode);
        }

        return classNode;
    }

    private UnresolvedType parseSuperclass() {
        if (accept(COLON)) {
            return nextUnresolvedType();
        } else {
            return UnresolvedType.OBJECT_TYPE;
        }
    }

    private String[] parseImplementsInterfaces() {
        if (accept(PLUS)) {
            List<String> interfaces = new ArrayList<>();

            consume().expectType(L_PAREN);
            while (true) {
                Token token = consume();
                String inter = token.expectType(IDENTIFIER).getContent();
                interfaces.add(inter);

                token = consume();
                if (token.getType() == R_PAREN) {
                    break;
                } else {
                    expect(COMMA);
                }
            }

            return interfaces.toArray(new String[interfaces.size()]);
        } else {
            return new String[0];
        }
    }

    private void parseMethodOrField(ClassNode classNode) {
        ModifierSet modifiers = new ModifierSet();

        boolean hasAccessModifier = false;
        while (peek().isModifier()) {
            modifiers.add(consume().getType().toModifier());
            hasAccessModifier = true;
        }

        if (!hasAccessModifier) {
            modifiers.setPublic(true);
        }

        Token token = peek();
        if (token.getType() == IDENTIFIER) {
            FieldNode field = parseField(modifiers);
            classNode.getFields().add(field);
        } else if (token.getType() == DEF_KEYWORD) {
            consume(); // consume 'def'
            MethodNode method = parseMethod(modifiers);
            classNode.getMethods().add(method);
        }
    }

    private FieldNode parseField(ModifierSet modifiers) {
        String name = consume().expectType(IDENTIFIER).getContent();
        expect(COLON); // consume ':'

        UnresolvedType type = nextUnresolvedType();
        FieldNode field = new FieldNode(name, type, modifiers);

        if (accept(ASSIGN)) {
            field.setDefaultValue(parseExpression());
        }

        return field;
    }

    private MethodNode parseMethod(ModifierSet modifiers) {
        String name = consume().expectType(IDENTIFIER).getContent();

        UnresolvedType type;
        if (accept(COLON)) {
            type = nextUnresolvedType();
        } else {
            type = UnresolvedType.VOID_TYPE;
        }

        List<Parameter> parameters = new ArrayList<>();
        if (accept(L_PAREN)) {
            while (peek().getType() != R_PAREN) {
                String parameterName = consume().expectType(IDENTIFIER).getContent();
                expect(COLON);
                UnresolvedType parameterType = nextUnresolvedType();

                parameters.add(new Parameter(parameterName, parameterType));

                accept(COMMA);
            }
            expect(R_PAREN);
        }

        MethodNode method = new MethodNode(name, type, parameters, modifiers);

        method.setBody(parseBody());

        return method;
    }

    private Body parseBody() {
        Body body = new Body();
        expect(L_BRACE);

        while (peek().getType() != R_BRACE) {
            body.addCode(parseStatement());
        }
        expect(R_BRACE);

        return body;
    }

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
        } else if (accept(EACH_KEYWORD)) {
            return parseEachStatement();
        } else if (accept(FOR_KEYWORD)) {
            return parseForStatement();
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

    private VarStmt parseLocalVariable() {
        String name = consume().expectType(IDENTIFIER).getContent();
        expect(COLON);

        UnresolvedType type = nextUnresolvedType();
        Expression value = accept(ASSIGN) ? parseExpression() : null;

        return new VarStmt(name, type, value);
    }

    public IfStmt parseIfStatement(boolean inverted) {
        Expression condition = parseExpression();
        if (inverted) {
            // TODO optimize
            condition = new NotExpr(condition);
        }

        if (accept(THEN_KEYWORD)) {
            Body ifBranch = new Body(parseStatement());
            Body elseBranch = accept(ELSE_KEYWORD) ? new Body(parseStatement()) : null;

            return new IfStmt(condition, ifBranch, new ArrayList<>(), elseBranch);
        }

        Body ifBranch = parseBody();
        List<IfStmt.ElseIf> elseIfs = new ArrayList<>();
        Body elseBranch = null;

        while (accept(ELSE_KEYWORD)) {
            if (accept(IF_KEYWORD)) {
                IfStmt.ElseIf elseIf = new IfStmt.ElseIf(
                        parseExpression(), parseBody()
                );
                elseIfs.add(elseIf);
            } else { // so meta
                if (elseBranch != null) throw new RuntimeException("There is already an else branch");
                elseBranch = parseBody();
            }
        }

        return new IfStmt(condition, ifBranch, elseIfs, elseBranch);
    }

    private WhileStmt parseWhileStatement(boolean inverted) {
        Expression condition = parseExpression();
        if (inverted) {
            condition = new NotExpr(condition);
        }

        Body body;
        if (accept(DO_KEYWORD)) {
            body = new Body(parseStatement());
        } else {
            body = parseBody();
        }

        return new WhileStmt(condition, body);
    }

    private EachStmt parseEachStatement() {
        String identifier = consume().expectType(IDENTIFIER).getContent();
        // "in" keyword (not really a keyword)
        consume().expectType(IDENTIFIER).expectContent("in");

       Expression expression = parseExpression();

        Body body;
        if (accept(DO_KEYWORD)) {
            body = new Body(parseStatement());
        } else {
            body = parseBody();
        }

        return new EachStmt(identifier, expression, body);
    }

    private ForStmt parseForStatement() {
        String ident = consume().expectType(IDENTIFIER).getContent();
        consume().expectType(IDENTIFIER).expectContent("in");

        Expression expr = parseExpression();
        if (!(expr instanceof RangeExpr))
            throw new RuntimeException("Expected range");
        RangeExpr range = (RangeExpr) expr;

        Body body;
        if (accept(DO_KEYWORD)) {
            body = new Body(parseStatement());
        } else {
            body = parseBody();
        }

        return new ForStmt(ident, range, body);
    }

}
