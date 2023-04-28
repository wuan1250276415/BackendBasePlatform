package pro.wuan.core.controller;//package pro.wuan.backendbaseplatform.controller;
//
//import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import pro.wuan.backendbaseplatform.exception.CustomException;
//import reactor.core.publisher.Mono;
//@RestController
//public class TestController {
//
//    @GetMapping("/test")
//    public String testException() {
//        int i = 1 / 0;
//        return "hello world";
//    }
//
//    private Mono<String> fallback(String param1, CallNotPermittedException e) {
//        return Mono.just("Handled the exception when the CircuitBreaker is open");
//    }
//
//    private Mono<String> fallback(String param1, CustomException e) {
//        return Mono.just("Handled the exception when the Bulkhead is full");
//    }
//
//    private Mono<String> fallback(String param1, NumberFormatException e) {
//        return Mono.just("Handled the NumberFormatException");
//    }
//
//    private Mono<String> fallback(String param1, Exception e) {
//        return Mono.just("Handled any other exception");
//    }
//}
