package cc.skyju.collector.collectors;

import cc.skyju.collector.data.Endpoint;
import cc.skyju.collector.data.RequestMethod;
import cc.skyju.collector.data.Service;
import cc.skyju.collector.helpers.AnnotationHelper;
import cc.skyju.collector.helpers.CommonHelper;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;

import javax.annotation.Nullable;
import java.util.List;
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
                String serviceName = Objects.requireNonNull(AnnotationHelper.getAnnotationFieldString(
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
                !methodDeclaration.isAnnotationPresent("PostMapping")) {
            return null;
        }
        Endpoint endpoint = new Endpoint();
        endpoint.setServiceName(serviceName);
        if (methodDeclaration.getAnnotationByName("RequestMapping").isPresent()) {
            NormalAnnotationExpr normalAnnotationExpr =
                    (NormalAnnotationExpr) methodDeclaration.getAnnotationByName("RequestMapping").get();
            switch (Objects.requireNonNull(AnnotationHelper.getAnnotationFieldString(normalAnnotationExpr,
                    "method"))) {
                case "RequestMethod.GET" -> endpoint.setMethod(RequestMethod.GET);
                case "RequestMethod.POST" -> endpoint.setMethod(RequestMethod.POST);
                case "RequestMethod.PUT" -> endpoint.setMethod(RequestMethod.PUT);
                case "RequestMethod.PATCH" -> endpoint.setMethod(RequestMethod.PATCH);
                case "RequestMethod.DELETE" -> endpoint.setMethod(RequestMethod.DELETE);
                case "RequestMethod.OPTIONS" -> endpoint.setMethod(RequestMethod.OPTIONS);
                case "RequestMethod.TRACE" -> endpoint.setMethod(RequestMethod.TRACE);
                default -> throw new RuntimeException("Unsupported method type");
            }
            endpoint.setUri(Objects.requireNonNull(CommonHelper.firstOrNull(
                    AnnotationHelper.getAnnotationFieldString(normalAnnotationExpr, "value"),
                    AnnotationHelper.getAnnotationFieldString(normalAnnotationExpr, "path")
            )));
        } else if (methodDeclaration.getAnnotationByName("GetMapping").isPresent()) {
            NormalAnnotationExpr normalAnnotationExpr =
                    (NormalAnnotationExpr) methodDeclaration.getAnnotationByName("GetMapping").get();
            endpoint.setMethod(RequestMethod.GET);
            endpoint.setUri(Objects.requireNonNull(CommonHelper.firstOrNull(
                    AnnotationHelper.getAnnotationFieldString(normalAnnotationExpr, "value"),
                    AnnotationHelper.getAnnotationFieldString(normalAnnotationExpr, "path")
            )));
        } else if (methodDeclaration.getAnnotationByName("PostMapping").isPresent()) {
            NormalAnnotationExpr normalAnnotationExpr =
                    (NormalAnnotationExpr) methodDeclaration.getAnnotationByName("PostMapping").get();
            endpoint.setMethod(RequestMethod.POST);
            endpoint.setUri(Objects.requireNonNull(CommonHelper.firstOrNull(
                    AnnotationHelper.getAnnotationFieldString(normalAnnotationExpr, "value"),
                    AnnotationHelper.getAnnotationFieldString(normalAnnotationExpr, "path")
            )));
        } else {
            throw new RuntimeException("Unsupported Mapping type");
        }
        return endpoint;
    }
}
