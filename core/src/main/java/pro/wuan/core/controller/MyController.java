package pro.wuan.core.controller;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class MyController {

    private final RestTemplate restTemplate;

    public MyController(RestTemplate restTemplate, ElasticsearchOperations elasticsearchOperations) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/call-my-service/{services}")
    public String callMyService(@PathVariable String services) {
        return restTemplate.getForObject("http://backendbaseplatform/services/" + services, String.class);
    }

}

