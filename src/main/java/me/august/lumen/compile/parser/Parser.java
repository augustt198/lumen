package me.august.lumen.compile.parser;

import me.august.lumen.common.Modifier;
import me.august.lumen.compile.parser.ast.ClassNode;
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

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public ProgramNode parseMain() {
        List<String> imports = new ArrayList<>();

        Token token = lexer.nextToken();

        ClassNode classNode = null;

        while (token.getType() != EOF) {
            if (token.getType() == IMPORT_KEYWORD) {
                imports.add(lexer.nextToken().expectType(IMPORT_PATH).getContent());
            } else if (token.getType() == CLASS_KEYWORD || token.hasAttribute(Attribute.ACC_MOD)) {
                if (classNode != null) throw new RuntimeException("Previous class definition found.");

                Modifier mod = Modifier.PUBLIC;
                if (token.getType() != CLASS_KEYWORD) {
                    mod = token.getType().toModifier();
                    lexer.nextToken().expectType(CLASS_KEYWORD); // consume `class` keyword
                }

                classNode = parseClass(mod);
            }

            token = lexer.nextToken();
        }

        ImportNode[] importNodes = new ImportNode[imports.size()];
        for (int i = 0; i < imports.size(); i++) {
            importNodes[i] = new ImportNode(imports.get(i));
        }
        return new ProgramNode(importNodes, classNode);
    }

    private ClassNode parseClass(Modifier mod) {
        String name = lexer.nextToken().expectType(IDENTIFIER).getContent();
        Token token = lexer.nextToken();

        String superClass = parseSuperclass(token);

        return new ClassNode(name, superClass, new String[]{}, mod);
    }

    private String parseSuperclass(Token token) {
        if (token.getType() == COLON) {
            return lexer.nextToken().expectType(IDENTIFIER, "Expected superclass identifier").getContent();
        } else {
            return OBJECT_CLASS_NAME;
        }
    }

}
