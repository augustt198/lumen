package me.august.lumen.compile.resolve;

import me.august.lumen.compile.analyze.method.MethodReference;
import me.august.lumen.compile.analyze.var.ClassVariable;
import me.august.lumen.compile.codegen.BuildContext;
import me.august.lumen.compile.parser.ast.Typed;
import me.august.lumen.compile.parser.ast.expr.*;
import me.august.lumen.compile.parser.ast.expr.owned.OwnedExpr;
import me.august.lumen.compile.resolve.data.ClassData;
import me.august.lumen.compile.resolve.data.FieldData;
import me.august.lumen.compile.resolve.data.MethodData;
import me.august.lumen.compile.resolve.impl.NameResolver;
import me.august.lumen.compile.resolve.lookup.DependencyManager;
import org.objectweb.asm.Type;

import java.util.List;

public class LumenTypeVisitor extends ResolvingVisitor {

    private DependencyManager deps;
    private BuildContext build;

    public LumenTypeVisitor(NameResolver resolver, DependencyManager deps, BuildContext build) {
        super(resolver);
        this.deps = deps;
        this.build = build;
    }

    @Override
    public void visitExpression(Expression expr) {
        if (expr instanceof OwnedExpr) {
            OwnedExpr owned = (OwnedExpr) expr;
            if (owned.getOwner() != null) {
                visitExpression(owned.getOwner());
                handleMember(owned, owned.getOwner());
            } else {
                handleRoot(owned);
            }
        } else {
            handleRoot(expr);
        }
    }

    private void handleRoot(Expression expr) {
        if (expr instanceof Typed) {
            visitType((Typed) expr);
        }

        if (expr instanceof StaticField) {
            StaticField stc = (StaticField) expr;

            ClassData cls = deps.lookup(stc.getResolvedType());
            if (cls == null)
                throw new RuntimeException("Unknown class: " + stc.getResolvedType());

            FieldData field = cls.getField(stc.getFieldName());
            stc.setType(field.getType());
        } else if (expr instanceof StaticMethodCall) {
            StaticMethodCall call = (StaticMethodCall) expr;

            ClassData cls = deps.lookup(call.getResolvedType());
            if (cls == null)
                throw new RuntimeException("Unknown class: " + call.getResolvedType());

            String methodName = call.getMethodName();
            MethodData method = methodForTypes(cls.getMethods(methodName), call.getParameters());
            if (method == null)
                throw new RuntimeException("Unknown method for types: " + methodName);

            call.setReturnType(method.getReturnType());
            Type methodType = methodType(method.getReturnType(), call.getParameters());

            call.setRef(new MethodReference.Static(
                call.getResolvedType(), methodName, methodType
            ));
        }

    }

    private void handleMember(OwnedExpr expr, Expression parent) {
        Type parentType = parent.expressionType();
        if (parentType.getSort() != Type.OBJECT)
            throw new RuntimeException("Tried to access member of a primitive type");

        String className = parentType.getClassName();
        ClassData cls = deps.lookup(className);

        // expr is a field
        if (expr instanceof IdentExpr) {
            IdentExpr ident = (IdentExpr) expr;
            handleField(ident, cls, className);
        } else if (expr instanceof MethodCallExpr) {
            MethodCallExpr call = (MethodCallExpr) expr;
            handleMethod(call, cls, className);
        }
    }

    private void handleField(IdentExpr ident, ClassData cls, String className) {
        String fieldName = ident.getIdentifier();

        FieldData field = cls.getField(fieldName);
        if (field == null)
            throw new RuntimeException("Unknown field: " + fieldName);

        ident.setRef(new ClassVariable(className, fieldName, field.getType()));
    }

    private void handleMethod(MethodCallExpr call, ClassData cls, String className) {
        String methodName = call.getIdentifier();
        List<MethodData> candidates = cls.getMethods(methodName);

        MethodData method = methodForTypes(candidates, call.getParams());
        if (method == null) throw new RuntimeException("Unknown method for types");

        Type methodType = methodType(method.getReturnType(), call.getParams());
        call.setRef(new MethodReference.Instance(className, methodName, methodType));
    }

    public static MethodData methodForTypes(List<MethodData> candidates, List<Expression> args) {
        outer:
        for (MethodData method : candidates) {
            Type[] types = method.getParamTypes();
            if (types.length != args.size()) continue;

            for (int i = 0; i < types.length; i++) {
                if (types[i].getSort() != args.get(i).expressionType().getSort())
                    continue outer;
            }

            return method;
        }

        return null;
    }

    public static Type methodType(Type ret, List<Expression> args) {
        Type[] types = new Type[args.size()];
        for (int i = 0; i < args.size(); i++) {
            types[i] = args.get(0).expressionType();
        }

        return Type.getMethodType(ret, types);
    }

}
