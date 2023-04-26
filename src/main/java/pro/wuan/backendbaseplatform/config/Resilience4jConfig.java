package pro.wuan.backendbaseplatform.config;

import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    @Bean
    public CircuitBreakerConfigCustomizer defaultCircuitBreakerConfigCustomizer() {
        return CircuitBreakerConfigCustomizer
                .of("default", builder -> builder
                        .slidingWindowSize(5)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofMillis(1000))
                        .slowCallRateThreshold(50)
                        .slowCallDurationThreshold(Duration.ofSeconds(2))
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .minimumNumberOfCalls(10)
                        .build());
    }
}

