package cc.skyju.collector.factories;

import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class TypeSolverFactory {
    public static TypeSolver getCombinedTypeSolver(String sourceRoot) {
        return new CombinedTypeSolver(
                new JavaParserTypeSolver(sourceRoot),
                new ReflectionTypeSolver()
        );
    }
}
