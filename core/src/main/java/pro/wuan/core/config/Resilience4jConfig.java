package pro.wuan.core.config;


import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class Resilience4jConfig {



    @Bean
public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
    return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build())
            .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
            .build());
}

@Bean
public Customizer<Resilience4JCircuitBreakerFactory> slowCustomizer() {
    return factory -> factory.configure(builder -> builder.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
            .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(2)).build()), "slow");
}

//    @Bean
//    public RegistryEventConsumer<CircuitBreaker> myRegistryEventConsumer() {
//
//        return new RegistryEventConsumer<>() {
//            @Override
//            public void onEntryAddedEvent(@NotNull EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
//                entryAddedEvent.getAddedEntry().getEventPublisher().onEvent(event -> log.info(event.toString()));
//            }
//
//            @Override
//            public void onEntryRemovedEvent(@NotNull EntryRemovedEvent<CircuitBreaker> entryRemoveEvent) {
//
//            }
//
//            @Override
//            public void onEntryReplacedEvent(@NotNull EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {
//
//            }
//        };
//    }
//
//    @Bean
//    public RegistryEventConsumer<Retry> myRetryRegistryEventConsumer() {
//
//        return new RegistryEventConsumer<>() {
//            @Override
//            public void onEntryAddedEvent(@NotNull EntryAddedEvent<Retry> entryAddedEvent) {
//                entryAddedEvent.getAddedEntry().getEventPublisher().onEvent(event -> log.info(event.toString()));
//            }
//
//            @Override
//            public void onEntryRemovedEvent(@NotNull EntryRemovedEvent<Retry> entryRemoveEvent) {
//
//            }
//
//            @Override
//            public void onEntryReplacedEvent(@NotNull EntryReplacedEvent<Retry> entryReplacedEvent) {
//
//            }
//        };
//    }
}

