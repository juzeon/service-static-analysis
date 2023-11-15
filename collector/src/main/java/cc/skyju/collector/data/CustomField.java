package cc.skyju.collector.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomField {
    private String name;
    private CustomResolvedType type;
}
