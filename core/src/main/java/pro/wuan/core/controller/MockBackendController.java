package pro.wuan.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class MockBackendController {

    private AtomicInteger errorCounter = new AtomicInteger(0);

    @GetMapping("/my-service/test")
    public ResponseEntity<String> test() {
        if (errorCounter.incrementAndGet() % 4 == 0) {
            return ResponseEntity.ok("Success");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }
}

