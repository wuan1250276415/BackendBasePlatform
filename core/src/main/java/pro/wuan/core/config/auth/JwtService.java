package pro.wuan.core.config.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
/**
 * This class provides the JWT service for the application.
 * It uses Spring Boot's @Service annotation to indicate that it's a service class.
 */
@Service
public class JwtService {

  /**
   * The secret key for JWT.
   */
  @Value("${application.security.jwt.secret-key}")
  private String secretKey;

  /**
   * The expiration time for JWT.
   */
  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;

  /**
   * The expiration time for JWT refresh token.
   */
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;

  /**
   * This method extracts the username from the JWT token.
   *
   * @param token the JWT token
   * @return the username
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * This method extracts a claim from the JWT token.
   *
   * @param token the JWT token
   * @param claimsResolver the function to resolve the claim
   * @return the claim
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * This method generates a JWT token for the user.
   *
   * @param userDetails the user details
   * @return the JWT token
   */
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  /**
   * This method generates a JWT token for the user with extra claims.
   *
   * @param extraClaims the extra claims
   * @param userDetails the user details
   * @return the JWT token
   */
  public String generateToken(
      Map<String, Object> extraClaims,
      UserDetails userDetails
  ) {
    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  /**
   * This method generates a JWT refresh token for the user.
   *
   * @param userDetails the user details
   * @return the JWT refresh token
   */
  public String generateRefreshToken(
      UserDetails userDetails
  ) {
    return buildToken(new HashMap<>(), userDetails, refreshExpiration);
  }

  /**
   * This method builds a JWT token for the user with extra claims and expiration time.
   *
   * @param extraClaims the extra claims
   * @param userDetails the user details
   * @param expiration the expiration time
   * @return the JWT token
   */
  private String buildToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails,
          long expiration
  ) {
    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  /**
   * This method checks if the JWT token is valid for the user.
   *
   * @param token the JWT token
   * @param userDetails the user details
   * @return true if the token is valid, false otherwise
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  /**
   * This method checks if the JWT token is expired.
   *
   * @param token the JWT token
   * @return true if the token is expired, false otherwise
   */
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * This method extracts the expiration time from the JWT token.
   *
   * @param token the JWT token
   * @return the expiration time
   */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * This method extracts all claims from the JWT token.
   *
   * @param token the JWT token
   * @return the claims
   */
  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  /**
   * This method gets the sign-in key for JWT.
   *
   * @return the sign-in key
   */
  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
