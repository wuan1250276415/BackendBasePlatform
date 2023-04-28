package pro.wuan.core.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MyService {

    private final RestTemplate restTemplate;

    public MyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "default", fallbackMethod = "fallbackMethod")
    @Cacheable(value = "users", key = "#root.methodName")
    public String callRemoteService() {
        // Make a remote call using RestTemplate or another HTTP client
        return restTemplate.getForObject("http://backendbaseplatform/services", String.class);
    }

    public String fallbackMethod(Exception e) {
        return "Fallback response";
    }
}

