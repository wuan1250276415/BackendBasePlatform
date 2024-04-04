package pro.wuan.core.controller;//package pro.wuan.backendbaseplatform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class TestController {

    @GetMapping("/test")
    public String testException() {
        int i = 1 / 0;
        return "hello world";
    }

}
