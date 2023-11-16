package cc.skyju.collector.factories;

import cc.skyju.collector.data.CustomResolvedType;

import java.util.ArrayList;

public class CustomResolvedTypeFactory {
    public static CustomResolvedType getByQualifiedName(String qualifiedName) {
        return new CustomResolvedType(false, false, false,
                qualifiedName, new ArrayList<>(), new ArrayList<>());
    }
}
