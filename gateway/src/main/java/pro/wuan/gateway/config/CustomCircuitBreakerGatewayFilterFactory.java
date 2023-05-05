package pro.wuan.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomCircuitBreakerGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CustomCircuitBreakerGatewayFilterFactory(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @Override
    public GatewayFilter apply(NameValueConfig config) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(config.getName());

        return (exchange, chain) -> {
            // 使用熔断器包装请求
            return Mono.fromCompletionStage(
                    circuitBreaker.executeCompletionStage(
                            () -> chain.filter(exchange).toFuture()
                    ).handle((result, throwable) -> {
                        if (throwable != null) {
                            // 如果熔断器触发，转发到fallback URI
                            ServerHttpRequest request = exchange.getRequest().mutate().path(config.getValue()).build();
                            ServerWebExchange mutatedExchange = exchange.mutate().request(request).build();
                            return chain.filter(mutatedExchange).toFuture();
                        }
                        return result;
                    })
            ).then().onErrorResume(throwable -> {
                // 如果熔断器触发，转发到fallback URI
                ServerHttpRequest request = exchange.getRequest().mutate().path(config.getValue()).build();
                ServerWebExchange mutatedExchange = exchange.mutate().request(request).build();
                return chain.filter(mutatedExchange);
            });
        };
    }


}



 