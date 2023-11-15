package cc.skyju.collector.helpers;

import cc.skyju.collector.data.CustomField;
import cc.skyju.collector.data.CustomResolvedType;
import cc.skyju.collector.factories.CustomResolvedTypeFactory;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.declarations.*;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionFieldDeclaration;
import com.github.javaparser.utils.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JParserHelper {
    public static CustomResolvedType getMethodType(MethodDeclaration methodDeclaration) {
        Type type = methodDeclaration.getType(); // Get the type of the method
        return resolveType(type.resolve());
    }

    private static CustomResolvedType resolveType(ResolvedType type) {
        if (type.isVoid()) {
            return new CustomResolvedType(true, false, "",
                    new ArrayList<>(), new ArrayList<>());
        }
        if (type.isPrimitive()) {
            return new CustomResolvedType(false, true, type.asPrimitive().getBoxTypeQName(),
                    new ArrayList<>(), new ArrayList<>());
        }
        if (type.isReferenceType()) {
            ResolvedReferenceType referenceType = type.asReferenceType();
            String qualifiedName = referenceType.getQualifiedName();
            CustomResolvedType customResolvedType = new CustomResolvedType(false, false,
                    qualifiedName, resolveFields(referenceType), new ArrayList<>());
            for (Pair<ResolvedTypeParameterDeclaration, ResolvedType> pair : referenceType.getTypeParametersMap()) {
                customResolvedType.getGenericName().add(resolveType(pair.b));
            }
            return customResolvedType;
        }
        throw new RuntimeException("Unsupported type: " + type);
    }

    private static List<CustomField> resolveFields(ResolvedReferenceType referenceType) {
        List<CustomField> fields = new ArrayList<>();
        for (ResolvedReferenceType ancestor : referenceType.getAllClassesAncestors()) {
            // Loop through all the declared fields of the ancestor
            inflateField(fields, ancestor);
        }
        inflateField(fields, referenceType);
        return fields;
    }

    private static void inflateField(List<CustomField> fields, ResolvedReferenceType referenceType) {
        if (referenceType.getTypeDeclaration().isPresent() && referenceType.getTypeDeclaration().get().isEnum()) {
            System.out.println(referenceType);
            ResolvedEnumDeclaration red = (ResolvedEnumDeclaration) referenceType.getTypeDeclaration().get();
            List<ResolvedEnumConstantDeclaration> constants = red.getEnumConstants();
            for (ResolvedEnumConstantDeclaration constant : constants) {
                // use String for enum constant
                fields.add(new CustomField(constant.getName(),
                        CustomResolvedTypeFactory.getByQualifiedName("java.lang.String")));
            }
            return;
        }
        for (ResolvedFieldDeclaration field : referenceType.getDeclaredFields()) {
            if (field instanceof ReflectionFieldDeclaration) {
                continue;
            }
            CustomResolvedType fieldType = resolveType(field.getType());
            fields.add(new CustomField(field.getName(), fieldType));
        }
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
