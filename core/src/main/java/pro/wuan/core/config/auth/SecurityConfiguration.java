/**
 * This class provides the security configuration for the application.
 * It uses Spring Boot's @Configuration, @EnableWebSecurity and @EnableMethodSecurity annotations to enable security and method-level security.
 * It uses Lombok's @RequiredArgsConstructor annotation to generate a constructor with required fields.
 */
package pro.wuan.core.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    /**
     * An instance of JwtAuthenticationFilter to handle JWT authentication.
     */
    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * An instance of AuthenticationProvider to provide authentication services.
     */
    private final AuthenticationProvider authenticationProvider;

    /**
     * An instance of LogoutHandler to handle logout requests.
     */
    private final LogoutHandler logoutHandler;

    /**
     * This method configures the security filter chain.
     * It uses Spring Boot's @Bean annotation to indicate that it's a bean to be managed by Spring.
     * It configures the HTTP security, including CSRF protection, request authorization, session management, authentication provider, and logout handling.
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/api/v1/auth/**"
                ).permitAll().anyRequest().authenticated());
        http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authenticationProvider(authenticationProvider).addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer.logoutUrl("/api/v1/auth/logout").addLogoutHandler(logoutHandler).logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()));
        return http.build();
    }
}