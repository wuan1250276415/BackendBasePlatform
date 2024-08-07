package pro.wuan.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${tellhow.datasource.ip}")
    private String test;

    @GetMapping("test")
    public String getTest() {
        return test;
    }
}
