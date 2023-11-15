package cc.skyju.collector.collectors;

import cc.skyju.collector.data.Endpoint;
import cc.skyju.collector.data.RequestMethod;
import cc.skyju.collector.data.Service;
import cc.skyju.collector.helpers.JParserHelper;
import cc.skyju.collector.helpers.CommonHelper;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FeignClientCollector {
    public void inflateServiceDependencies(Service service) {
        for (CompilationUnit cu : service.getCuList()) {
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classOrInterfaceDeclaration -> {
                if (classOrInterfaceDeclaration.getAnnotationByName("FeignClient").isEmpty()) {
                    return;
                }
                NormalAnnotationExpr feignClientAnnotation = (NormalAnnotationExpr)
                        classOrInterfaceDeclaration.getAnnotationByName("FeignClient").get();
                String serviceName = Objects.requireNonNull(JParserHelper.getAnnotationFieldString(
                        feignClientAnnotation, "name"));
                for (MethodDeclaration methodDeclaration : classOrInterfaceDeclaration.getMethods()) {
                    Endpoint endpoint = getEndpointFromMethod(serviceName, methodDeclaration);
                    if (endpoint != null) {
                        service.getDependencies().add(endpoint);
                    }
                }
            });
        }
    }

    public void inflateServiceDependencies(List<Service> serviceList) {
        for (Service service : serviceList) {
            inflateServiceDependencies(service);
        }
    }

    public @Nullable Endpoint getEndpointFromMethod(String serviceName, MethodDeclaration methodDeclaration) {
        // Check if the method has any of the annotations
        if (!methodDeclaration.isAnnotationPresent("RequestMapping") &&
                !methodDeclaration.isAnnotationPresent("GetMapping") &&
                !methodDeclaration.isAnnotationPresent("PostMapping") &&
                !methodDeclaration.isAnnotationPresent("PutMapping") &&
                !methodDeclaration.isAnnotationPresent("PatchMapping") &&
                !methodDeclaration.isAnnotationPresent("DeleteMapping")) {
            return null;
        }
        Endpoint endpoint = new Endpoint();
        endpoint.setServiceName(serviceName);
        endpoint.setResponseType(JParserHelper.getMethodType(methodDeclaration));
        if (methodDeclaration.getAnnotationByName("RequestMapping").isPresent()) {
            NormalAnnotationExpr normalAnnotationExpr =
                    (NormalAnnotationExpr) methodDeclaration.getAnnotationByName("RequestMapping").get();
            switch (Objects.requireNonNull(JParserHelper.getAnnotationFieldString(normalAnnotationExpr,
                    "method"))) {
                case "RequestMethod.GET" -> endpoint.setMethod(RequestMethod.GET);
                case "RequestMethod.POST" -> endpoint.setMethod(RequestMethod.POST);
                case "RequestMethod.PUT" -> endpoint.setMethod(RequestMethod.PUT);
                case "RequestMethod.PATCH" -> endpoint.setMethod(RequestMethod.PATCH);
                case "RequestMethod.DELETE" -> endpoint.setMethod(RequestMethod.DELETE);
                default -> throw new RuntimeException("Unsupported method type");
            }
            endpoint.setUri(Objects.requireNonNull(CommonHelper.firstOrNull(
                    JParserHelper.getAnnotationFieldString(normalAnnotationExpr, "value"),
                    JParserHelper.getAnnotationFieldString(normalAnnotationExpr, "path")
            )));
        } else {
            String annotationName = "";
            Map<String, RequestMethod> mapping2requestMethod = Map.of("GetMapping", RequestMethod.GET,
                    "PostMapping", RequestMethod.POST,
                    "PutMapping", RequestMethod.PUT,
                    "PatchMapping", RequestMethod.PATCH,
                    "DeleteMapping", RequestMethod.DELETE);
            if (methodDeclaration.getAnnotationByName("GetMapping").isPresent()) {
                annotationName = "GetMapping";
            } else if (methodDeclaration.getAnnotationByName("PostMapping").isPresent()) {
                annotationName = "PostMapping";
            } else if (methodDeclaration.getAnnotationByName("PutMapping").isPresent()) {
                annotationName = "PutMapping";
            } else if (methodDeclaration.getAnnotationByName("PatchMapping").isPresent()) {
                annotationName = "PatchMapping";
            } else if (methodDeclaration.getAnnotationByName("DeleteMapping").isPresent()) {
                annotationName = "DeleteMapping";
            }
            //noinspection OptionalGetWithoutIsPresent
            NormalAnnotationExpr normalAnnotationExpr =
                    (NormalAnnotationExpr) methodDeclaration.getAnnotationByName(annotationName).get();
            endpoint.setMethod(mapping2requestMethod.get(annotationName));
            endpoint.setUri(Objects.requireNonNull(CommonHelper.firstOrNull(
                    JParserHelper.getAnnotationFieldString(normalAnnotationExpr, "value"),
                    JParserHelper.getAnnotationFieldString(normalAnnotationExpr, "path")
            )));
        }
        return endpoint;
    }
}
