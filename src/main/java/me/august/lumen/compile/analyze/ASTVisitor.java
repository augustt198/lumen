package me.august.lumen.compile.analyze;

import me.august.lumen.compile.parser.ast.ClassNode;
import me.august.lumen.compile.parser.ast.FieldNode;
import me.august.lumen.compile.parser.ast.ProgramNode;
import me.august.lumen.compile.parser.ast.expr.*;
import me.august.lumen.compile.parser.ast.stmt.Body;
import me.august.lumen.compile.parser.ast.stmt.VarStmt;

/**
 * Visitor pattern used for traversing the AST.
 *
 * Methods must be invoked in this order:
 * visitProgram
 *   visitClass
 *     (
 *        visitField |
 *        visitMethod (
 *           (visitBody ... visitBodyEnd | visitVar | visitExpression)*
 *        )*
 *     )*
 *   visitClassEnd
 * visitProgramEnd
 */
public interface ASTVisitor {

    default void visitProgram(ProgramNode program) {}
    default void visitProgramEnd(ProgramNode program) {}

    default void visitClass(ClassNode cls) {}
    default void visitClassEnd(ClassNode cls) {}

    default void visitField(FieldNode field) {}

    default void visitMethod(MethodNode method) {}
    default void visitMethodEnd(MethodNode method) {}

    default void visitBody(Body body) {}
    default void visitBodyEnd(Body body) {}

    default void visitVar(VarStmt var) {}

    default void visitExpression(Expression expr) {}

    @Deprecated
    default void visitIdentifier(IdentExpr expr) {}

    @Deprecated
    default void visitStaticField(StaticField sf) {}

    @Deprecated
    default void visitStaticMethodCall(StaticMethodCall sf) {}

}
