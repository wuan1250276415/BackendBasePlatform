/**
 * This class is a controller for handling authentication requests.
 * It uses Spring Boot's @RestController annotation to handle HTTP requests.
 */
package pro.wuan.core.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.core.auth.pojo.AuthenticationRequest;
import pro.wuan.core.auth.pojo.AuthenticationResponse;
import pro.wuan.core.auth.service.AuthenticationService;
import pro.wuan.core.auth.pojo.RegisterRequest;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    /**
     * An instance of AuthenticationService to handle the business logic related to authentication.
     */
    private final AuthenticationService service;

    /**
     * This method handles the registration requests.
     * It takes a RegisterRequest object as input and returns a ResponseEntity with an AuthenticationResponse.
     *
     * @param request the registration request
     * @return a ResponseEntity with an AuthenticationResponse
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    /**
     * This method handles the authentication requests.
     * It takes an AuthenticationRequest object as input and returns a ResponseEntity with an AuthenticationResponse.
     *
     * @param request the authentication request
     * @return a ResponseEntity with an AuthenticationResponse
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    /**
     * This method handles the refresh token requests.
     * It takes a HttpServletRequest and HttpServletResponse as input and refreshes the token.
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws IOException if an input or output exception occurred
     */
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }
}