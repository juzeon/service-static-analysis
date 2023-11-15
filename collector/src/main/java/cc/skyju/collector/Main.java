package cc.skyju.collector;

import cc.skyju.collector.collectors.FeignClientCollector;
import cc.skyju.collector.data.Endpoint;
import cc.skyju.collector.data.Service;
import cc.skyju.collector.factories.ServiceFactory;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    public void run(String repoRoot) throws IOException {
        List<Service> services = ServiceFactory.collectServicesFromRepo(repoRoot);
        new FeignClientCollector().inflateServiceDependencies(services);
        for (Service service : services) {
            System.out.println(service);
        }
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        Gson gson = builder.create();
        String json = gson.toJson(services);
        FileWriter writer = new FileWriter("services.json");
        writer.write(json);
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        new Main().run(args[0]);
    }
}