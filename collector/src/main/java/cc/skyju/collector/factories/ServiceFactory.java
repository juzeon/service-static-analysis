package cc.skyju.collector.factories;

import cc.skyju.collector.data.Service;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServiceFactory {
    public static List<Service> collectServicesFromRepo(String repoDir) throws IOException {
        File dir = new File(repoDir);
        List<Service> services = new ArrayList<>();
        for (File subFile : Objects.requireNonNull(dir.listFiles())) {
            if (!subFile.isDirectory()) {
                continue;
            }
            File[] files = subFile.listFiles();
            assert files != null;
            List<File> sourceRoots = new ArrayList<>();
            for (File f : files) {
                final File javaDirFile = Paths.get(f.getAbsolutePath(), "main", "java").toFile();
                if (javaDirFile.isDirectory()) {
                    sourceRoots.add(javaDirFile);
                }
            }
            for (File sourceRoot : sourceRoots) {
                SourceRoot sr = new SourceRoot(sourceRoot.toPath());
                sr.getParserConfiguration()
                        .setSymbolResolver(new JavaSymbolSolver(TypeSolverFactory
                                .getCombinedTypeSolver(sourceRoots.stream().map(File::getAbsolutePath).toList())));
                sr.tryToParse();
                services.add(new Service(subFile.getName(), sourceRoot.getAbsolutePath(), sr.getCompilationUnits(),
                        new ArrayList<>()));
            }
        }
        return services;
    }
}
