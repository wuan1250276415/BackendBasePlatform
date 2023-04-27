package pro.wuan.backendbaseplatform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public Mono<String> fallback() {
        return Mono.just("Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/slowApi")
    public ResponseEntity<String> slowApi() throws InterruptedException {
        // 模拟一个5秒的延迟
        Thread.sleep(5000);

        return ResponseEntity.ok("Success");
    }


}

