/**
 * This class provides the application configuration for authentication.
 * It uses Spring Boot's @Configuration annotation to indicate that it's a configuration class.
 */
package pro.wuan.core.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pro.wuan.core.user.UserRepository;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  /**
   * An instance of UserRepository to handle the user-related operations.
   */
  private final UserRepository repository;

  /**
   * This method provides the user details service.
   * It takes a username as input and returns the user details.
   *
   * @return a UserDetailsService instance
   */
  @Bean
  public UserDetailsService userDetailsService() {
    return username -> repository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  /**
   * This method provides the authentication provider.
   * It uses DaoAuthenticationProvider to authenticate the user with the user details service and password encoder.
   *
   * @return an AuthenticationProvider instance
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /**
   * This method provides the authentication manager.
   * It uses the authentication configuration to get the authentication manager.
   *
   * @param config the authentication configuration
   * @return an AuthenticationManager instance
   * @throws Exception if an error occurred while getting the authentication manager
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * This method provides the password encoder.
   * It uses BCryptPasswordEncoder to encode the password.
   *
   * @return a PasswordEncoder instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}