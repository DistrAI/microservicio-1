package com.sw.GestorAPI.resolver;

import com.sw.GestorAPI.dto.AuthResponse;
import com.sw.GestorAPI.dto.LoginRequest;
import com.sw.GestorAPI.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

/**
 * Resolver GraphQL para operaciones de autenticación
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthResolver {

    private final AuthService authService;

    /**
     * Mutation para realizar login
     */
    @MutationMapping
    public AuthResponse login(@Argument String email, @Argument String password) {
        log.info("GraphQL Mutation: login con email: {}", email);
        
        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        return authService.login(request);
    }
}
