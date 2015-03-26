package me.august.lumen.compile.resolve;

import me.august.lumen.compile.analyze.ASTVisitor;
import me.august.lumen.compile.analyze.types.BasicTypeInfo;
import me.august.lumen.compile.analyze.types.TypeInfo;
import me.august.lumen.compile.ast.*;
import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.ast.stmt.VarStmt;
import me.august.lumen.compile.resolve.type.BasicType;
import org.objectweb.asm.Type;

/**
 * A common visitor pattern for all AST nodes associated with a type.
 */
public class TypeVisitor implements ASTVisitor {

    private TypeResolver resolver;

    public TypeVisitor(TypeResolver resolver) {
        this.resolver = resolver;
    }

    private void visitType(TypeInfo typeInfo) {
        typeInfo.resolve(resolver);
    }

    public void visitType(TypeInfoProducer producer) {
        visitType(producer.getTypeInfo());
    }

    @Override
    public void visitVar(VarStmt var) {
        visitType(var);
    }

    @Override
    public void visitField(FieldNode field) {
        visitType(field);
    }

    @Override
    public void visitMethod(MethodNode method) {
        visitType(method);

        method.getParameters().forEach(this::visitType);
    }

    @Override
    public void visitExpression(Expression expr) {
        if (expr instanceof TypeInfoProducer) {
            visitType((TypeInfoProducer) expr);
        }
    }

    @Override
    public void visitClass(ClassNode cls) {
        visitType(cls);
    }
}
