package pro.wuan.core.auth.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pro.wuan.core.auth.pojo.AuthenticationRequest;
import pro.wuan.core.auth.pojo.AuthenticationResponse;
import pro.wuan.core.auth.pojo.RegisterRequest;
import pro.wuan.core.config.auth.JwtService;
import pro.wuan.core.role.RoleRepository;
import pro.wuan.core.token.TokenRepository;
import pro.wuan.core.user.UserRepository;
import pro.wuan.feignapi.userapi.entity.CurrentUser;
import pro.wuan.feignapi.userapi.entity.Token;
import pro.wuan.feignapi.userapi.entity.TokenType;
import pro.wuan.feignapi.userapi.entity.User;

import java.io.IOException;
import java.util.HashSet;

/**
 * This class provides the authentication service for the application.
 * It uses Spring Boot's @Service annotation to indicate that it's a service class.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * This method handles the registration of a new user.
     * It takes a RegisterRequest object as input and returns an AuthenticationResponse.
     *
     * @param request the registration request
     * @return an AuthenticationResponse with the access and refresh tokens
     */
    public AuthenticationResponse register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .status(true)
                .roles(request.getRole())
                .build();
        var roles = roleRepository.saveAll(user.getRoles());
        user.setRoles(new HashSet<>(roles));
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        user.setToken(jwtToken);
        CurrentUser.set(user); // set the current user
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * This method handles the authentication of a user.
     * It takes an AuthenticationRequest object as input and returns an AuthenticationResponse.
     *
     * @param request the authentication request
     * @return an AuthenticationResponse with the access and refresh tokens
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        user.setToken(jwtToken);
        CurrentUser.set(user); // set the current user
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * This method saves the user token.
     *
     * @param user the user
     * @param jwtToken the JWT token
     */
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    /**
     * This method revokes all tokens of a user.
     *
     * @param user the user
     */
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    /**
     * This method handles the refresh token requests.
     * It takes a HttpServletRequest and HttpServletResponse as input and refreshes the token.
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws IOException if an input or output exception occurred
     */
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var user = this.repository.findByUsername(username)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                user.setToken(accessToken);
                CurrentUser.set(user); // set the current user
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
