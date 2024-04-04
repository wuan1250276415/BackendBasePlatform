/**
 * This class provides the logout service for the application.
 * It implements Spring's LogoutHandler to handle the logout process.
 * It uses Spring Boot's @Service annotation to indicate that it's a service class.
 */
package pro.wuan.core.config.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import pro.wuan.core.token.TokenRepository;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  /**
   * An instance of TokenRepository to handle token-related operations.
   */
  private final TokenRepository tokenRepository;

  /**
   * This method handles the logout process.
   * It takes a HttpServletRequest, HttpServletResponse, and Authentication as input and logs out the user.
   *
   * @param request the HttpServletRequest
   * @param response the HttpServletResponse
   * @param authentication the Authentication
   */
  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    jwt = authHeader.substring(7);
    var storedToken = tokenRepository.findByToken(jwt)
        .orElse(null);
    if (storedToken != null) {
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      tokenRepository.save(storedToken);
      SecurityContextHolder.clearContext();
    }
  }
}