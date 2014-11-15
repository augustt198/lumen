package me.august.lumen.compile.parser;

import me.august.lumen.common.Modifier;
import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.FieldNode;
import me.august.lumen.compile.parser.ast.ImportNode;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.scanner.Lexer;
import me.august.lumen.compile.scanner.Token;

import java.util.ArrayList;
import java.util.List;

import static me.august.lumen.compile.scanner.Type.*;

public class Parser {

    private static final String OBJECT_CLASS_NAME = Object.class.getName();

    private Lexer lexer;
    private Token current;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    private Token next() {
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

        return new FieldNode(name, type, mods);
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

}
