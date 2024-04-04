/**
 * This class provides the JPA configuration for the application.
 * It uses Spring Boot's @Configuration annotation to indicate that it's a configuration class.
 * It uses Spring Boot's @EnableJpaAuditing annotation to enable JPA auditing.
 */
package pro.wuan.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import pro.wuan.core.config.security.SpringSecurityAuditorAware;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {

    /**
     * This method provides the auditor aware for JPA auditing.
     * It returns a new instance of SpringSecurityAuditorAware.
     *
     * @return an AuditorAware<Integer> instance
     */
    @Bean
    public AuditorAware<Integer> auditorAware() {
        return new SpringSecurityAuditorAware();
    }

}