package cc.skyju.collector.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Endpoint {
    private String serviceName;
    private RequestMethod method;
    private String uri;
}
