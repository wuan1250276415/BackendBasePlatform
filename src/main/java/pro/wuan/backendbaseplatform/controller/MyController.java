package pro.wuan.backendbaseplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import pro.wuan.backendbaseplatform.service.MyService;

@RestController
public class MyController {

    private final RestTemplate restTemplate;

    @Autowired
    private MyService myService;

    public MyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/call-my-service/{services}")
    public String callMyService(@PathVariable String services) {
        return restTemplate.getForObject("http://backendbaseplatform/services/"+services, String.class);
    }

    @GetMapping("myservice/test")
    public String testException() {
        return myService.callRemoteService();
    }
}

