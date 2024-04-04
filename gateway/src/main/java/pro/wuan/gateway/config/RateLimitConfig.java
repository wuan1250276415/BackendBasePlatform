package pro.wuan.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * This class is a configuration class for rate limiting in a Spring Boot application.
 * It uses Spring Cloud Gateway for rate limiting.
 */
@Configuration
public class RateLimitConfig {

    /**
     * This method is a Spring Boot configuration method that produces a bean to be managed by the Spring container.
     * The bean is an instance of KeyResolver, an interface provided by Spring Cloud Gateway for rate limiting.
     * The KeyResolver instance is created using a lambda function that takes a ServerWebExchange object as a parameter.
     * This object represents a server-side HTTP request/response exchange.
     * The lambda function returns a Mono object that emits the IP address of the client making the request.
     * The IP address is used as the key for rate limiting.
     *
     * @return a KeyResolver instance that uses the client's IP address as the key for rate limiting
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
    }
}