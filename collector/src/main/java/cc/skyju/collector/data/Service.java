package cc.skyju.collector.data;

import com.github.javaparser.ast.CompilationUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import java.util.List;

@Data
@AllArgsConstructor
@ToString
public class Service {
    private String name;
    private String sourceRoot;
    @ToString.Exclude
    private List<CompilationUnit> cuList;
    private List<Endpoint> dependencies;
}
