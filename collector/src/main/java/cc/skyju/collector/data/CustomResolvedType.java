package cc.skyju.collector.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResolvedType {
    private Boolean isVoid;
    private Boolean isPrimitive;
    private String baseName;
    private List<CustomField> fields;
    private List<CustomResolvedType> genericName;
}
