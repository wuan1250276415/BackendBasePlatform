package pro.wuan.core.base.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class FallbackController {

    private final AtomicInteger errorCounter = new AtomicInteger(0);
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
    @GetMapping("/my-service/test")
    public ResponseEntity<String> test() {
        if (errorCounter.incrementAndGet() % 4 == 0) {
            return ResponseEntity.ok("Success");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }


}

