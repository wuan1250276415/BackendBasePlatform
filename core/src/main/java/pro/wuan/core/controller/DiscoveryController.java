package pro.wuan.core.controller;

import lombok.experimental.Delegate;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Component
public class DiscoveryController {

    private final DiscoveryClient discoveryClient;

    public DiscoveryController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Delegate
    @GetMapping("/services")
    public List<String> services() {
        return discoveryClient.getServices();
    }


    @GetMapping("/services/{serviceId}")
    public List<ServiceInstance> serviceInstances(@PathVariable String serviceId) {
        return discoveryClient.getInstances(serviceId);
    }
}

