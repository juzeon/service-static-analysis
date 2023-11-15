package cc.skyju.collector.factories;

import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.util.List;

public class TypeSolverFactory {
    public static TypeSolver getCombinedTypeSolver(List<String> sourceRoots) {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver(
                new ReflectionTypeSolver()
        );
        for (String sourceRoot : sourceRoots) {
            combinedTypeSolver.add(new JavaParserTypeSolver(sourceRoot));
        }
        return combinedTypeSolver;
    }
}
