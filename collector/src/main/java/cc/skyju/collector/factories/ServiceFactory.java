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
            for (File f : files) {
                final File javaDirFile = Paths.get(f.getAbsolutePath(), "main", "java").toFile();
                if (javaDirFile.isDirectory()) {
                    SourceRoot sr = new SourceRoot(javaDirFile.toPath());
                    sr.getParserConfiguration()
                            .setSymbolResolver(new JavaSymbolSolver(TypeSolverFactory
                                    .getCombinedTypeSolver(javaDirFile.getAbsolutePath())));
                    sr.tryToParse();
                    services.add(new Service(subFile.getName(), javaDirFile.getAbsolutePath(), sr.getCompilationUnits(),
                            new ArrayList<>()));
                }
            }
        }
        return services;
    }
}
