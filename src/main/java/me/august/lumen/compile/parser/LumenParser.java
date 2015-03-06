package me.august.lumen.compile.parser;

import me.august.lumen.common.ModifierSet;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.ast.*;
import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.ast.expr.NotExpr;
import me.august.lumen.compile.ast.expr.RangeExpr;
import me.august.lumen.compile.ast.stmt.*;
import me.august.lumen.compile.resolve.type.UnresolvedType;
import me.august.lumen.compile.scanner.Token;
import me.august.lumen.compile.scanner.TokenSource;
import me.august.lumen.compile.scanner.tokens.ImportPathToken;

import java.util.ArrayList;
import java.util.List;

import static me.august.lumen.compile.scanner.TokenType.*;

public class LumenParser extends ExpressionParser {

    public LumenParser(TokenSource tokenSource, BuildContext context) {
        super(tokenSource, context);
    }

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
        ClassNode classNode         = null;

        startRecording();
        Token token                 = consume();
        while (token.getType() != EOF) {
            // Handle import declaration
            if (token.getType() == IMPORT_KEYWORD) {

                ImportPathToken pathToken = (ImportPathToken) consume();
                ImportNode node = new ImportNode(pathToken.getPath(), pathToken.getClasses());
                imports.add(node);

                endRecording(node);

            // Handle class declaration
            } else if (token.getType() == CLASS_KEYWORD || token.isModifier()) {
                ModifierSet modifiers = new ModifierSet();

                if (token.isModifier()) {
                    modifiers.add(token.getType().toModifier());
                    modifiers.merge(parseModifiers());
                    expect(CLASS_KEYWORD);
                } else {
                    modifiers.setPublic(true);
                }

                classNode = parseClass(modifiers);
                endRecording(classNode);
            }

            startRecording();
            token = consume();
        }
        stopRecording();

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
                    token.expectType(COMMA);
                }
            }

            return interfaces.toArray(new String[interfaces.size()]);
        } else {
            return new String[0];
        }
    }

    private void parseMethodOrField(ClassNode classNode) {
        ModifierSet modifiers = new ModifierSet();

        startRecording();
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

            endRecording(field);
        } else if (token.getType() == DEF_KEYWORD) {
            consume(); // consume 'def'
            MethodNode method = parseMethod(modifiers);
            classNode.getMethods().add(method);

            endRecording(method);
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
        startRecording();

        expect(L_BRACE);

        while (peek().getType() != R_BRACE) {
            body.addCode(parseStatement());
        }
        expect(R_BRACE);

        return endRecording(body);
    }

    private CodeBlock parseStatement() {
        startRecording();

        CodeBlock code;
        if (accept(VAR_KEYWORD)) {
            code = parseLocalVariable();
        } else if (accept(L_BRACE)) {
            code = parseBody();
        } else if (accept(IF_KEYWORD)) {
            code = parseIfStatement(false);
        } else if (accept(UNLESS_KEYWORD)) {
            code = parseIfStatement(true);
        } else if (accept(WHILE_KEYWORD)) {
            code = parseWhileStatement(false);
        } else if (accept(UNTIL_KEYWORD)) {
            code = parseWhileStatement(true);
        } else if (accept(EACH_KEYWORD)) {
            code = parseEachStatement();
        } else if (accept(FOR_KEYWORD)) {
            code = parseForStatement();
        } else if (accept(BREAK_KEYWORD)) {
            code = new BreakStmt();
        } else if (accept(NEXT_KEYWORD)) {
            code = new NextStmt();
        } else if (accept(RETURN_KEYWORD)) {
            code = new ReturnStmt(parseExpression());
        } else {
            code = parseExpression();
        }

        return endRecording(code);
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
        if (!(expr instanceof RangeExpr)) {
            getBuildContext().error("Expected range", false, expr);
        }

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
