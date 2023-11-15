package cc.skyju.collector;

import cc.skyju.collector.collectors.FeignClientCollector;
import cc.skyju.collector.data.Service;
import cc.skyju.collector.factories.ServiceFactory;

import java.io.IOException;
import java.util.List;

public class Main {
    public void run(String repoRoot) throws IOException {
        List<Service> services = ServiceFactory.collectServicesFromRepo(repoRoot);
        new FeignClientCollector().inflateServiceDependencies(services);
        for (Service service : services) {
            System.out.println(service);
        }
    }

    public static void main(String[] args) throws IOException {
        new Main().run(args[0]);
    }
}