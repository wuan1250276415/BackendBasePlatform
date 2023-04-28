package pro.wuan.core.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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
    @CircuitBreaker(name = "services")
    @Bulkhead(name = "services")
    @Retry(name = "services")
    public List<String> services() {
        return discoveryClient.getServices();
    }


    @GetMapping("/services/{serviceId}")
    @CircuitBreaker(name = "services")
    @Bulkhead(name = "services")
    @Retry(name = "services")
    public List<ServiceInstance> serviceInstances(@PathVariable String serviceId) {
        return discoveryClient.getInstances(serviceId);
    }
}

