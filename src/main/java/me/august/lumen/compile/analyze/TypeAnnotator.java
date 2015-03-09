package me.august.lumen.compile.analyze;

import me.august.lumen.compile.analyze.types.TypeInfo;
import me.august.lumen.compile.ast.ClassNode;
import me.august.lumen.compile.ast.FieldNode;
import me.august.lumen.compile.ast.MethodNode;
import me.august.lumen.compile.ast.TypeInfoProducer;
import me.august.lumen.compile.ast.expr.Expression;
import me.august.lumen.compile.ast.stmt.VarStmt;
import me.august.lumen.compile.resolve.TypeResolver;

public class TypeAnnotator extends ASTAnnotator<TypeInfo> implements ASTVisitor {

    private TypeResolver resolver;

    public TypeAnnotator(TypeResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void visitExpression(Expression expr) {
        handleNode(expr);
    }

    @Override
    public void visitMethod(MethodNode method) {
        handleNode(method);
    }

    @Override
    public void visitClass(ClassNode cls) {
        handleNode(cls);
    }

    @Override
    public void visitField(FieldNode field) {
        handleNode(field);
    }

    @Override
    public void visitVar(VarStmt var) {
        handleNode(var);
    }

    private void handleNode(Object obj) {
        if (obj instanceof TypeInfoProducer) {
            TypeInfoProducer producer = (TypeInfoProducer) obj;
            TypeInfo info = producer.getTypeInfo();

            info.resolve(resolver);

            setValue(producer, info);
        }
    }

}
