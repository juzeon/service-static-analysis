package cc.skyju.collector.helpers;

import cc.skyju.collector.data.ResolvedType;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JParserHelper {
    public static ResolvedType getMethodType(MethodDeclaration methodDeclaration) {
        Type type = methodDeclaration.getType(); // Get the type of the method
        return resolveType(type);
    }

    private static ResolvedType resolveType(Type type) {
        if (type.isVoidType()) {
            return new ResolvedType(true, false, "", new ArrayList<>());
        }
        if (type.isPrimitiveType()) {
            return new ResolvedType(false, true, type.asPrimitiveType().asString(), new ArrayList<>());
        }
        if (type.isClassOrInterfaceType()) {
            ClassOrInterfaceType classOrInterfaceType = type.asClassOrInterfaceType();
            String qualifiedName = classOrInterfaceType.resolve().asReferenceType().getQualifiedName();
            ResolvedType resolvedType = new ResolvedType(false, false, qualifiedName, new ArrayList<>());
            if (classOrInterfaceType.getTypeArguments().isPresent()) { // Check if the type is a generic type
                List<Type> typeArguments = classOrInterfaceType.getTypeArguments().get(); // Get the type arguments of the generic type
                for (int i = 0; i < typeArguments.size(); i++) { // Iterate over the type arguments
                    resolvedType.getGenericName().add(resolveType(typeArguments.get(i)));
                }
            }
            return resolvedType;
        }
        throw new RuntimeException("Unsupported type: " + type);
    }

    public static @Nullable String getAnnotationFieldString(NormalAnnotationExpr normalAnnotationExpr, String fieldName) {
        NodeList<MemberValuePair> pairs = normalAnnotationExpr.getPairs();
        // Iterate over the pairs and print their names and values
        for (MemberValuePair pair : pairs) {
            if (!pair.getName().asString().equals(fieldName)) {
                continue;
            }
            if (pair.getValue().isLiteralStringValueExpr()) {
                return pair.getValue().asLiteralStringValueExpr().getValue();
            }
            return pair.getValue().toString();
        }
        return null;
    }

    public static @Nullable LiteralExpr getAnnotationField(NormalAnnotationExpr normalAnnotationExpr, String fieldName) {
        NodeList<MemberValuePair> pairs = normalAnnotationExpr.getPairs();
        // Iterate over the pairs and print their names and values
        for (MemberValuePair pair : pairs) {
            if (!pair.getName().asString().equals(fieldName)) {
                continue;
            }
            // Get the value of the pair, which is an expression
            Expression value = pair.getValue();
            // Check if the value is a literal expression, such as a string or a number
            if (value.isLiteralExpr()) {
                // Cast it to a literal expression and get the inner value as a string
                return (LiteralExpr) value;
            } else if (value.isNameExpr()) {
                // Cast it to a name expression and get the name
                NameExpr nameExpr = (NameExpr) value;
                // Resolve the name to get the declaration of the variable or constant
                ResolvedValueDeclaration resolvedValueDeclaration = nameExpr.resolve();
                // Check if the declaration is a variable declaration
                if (resolvedValueDeclaration.isVariable()) {
                    // Cast it to a variable declaration and get the initializer
                    VariableDeclarator variableDeclarator = (VariableDeclarator) resolvedValueDeclaration;
                    Optional<Expression> initializer = variableDeclarator.getInitializer();
                    // Check if the initializer is present
                    if (initializer.isPresent()) {
                        // Get the expression of the initializer
                        Expression initializerExpr = initializer.get();
                        // Check if the expression is a literal expression
                        if (initializerExpr.isLiteralExpr()) {
                            // Cast it to a literal expression and get the inner value as a string
                            return (LiteralExpr) initializerExpr;
                        }
                    }
                }
            }
            break;
        }
        return null;
    }
}
