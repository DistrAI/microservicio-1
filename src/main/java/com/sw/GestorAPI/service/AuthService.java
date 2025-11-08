package com.sw.GestorAPI.service;

import com.sw.GestorAPI.dto.AuthResponse;
import com.sw.GestorAPI.dto.LoginRequest;
import com.sw.GestorAPI.entity.Usuario;
import com.sw.GestorAPI.repository.UsuarioRepository;
import com.sw.GestorAPI.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de autenticación
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    /**
     * Realiza el login de un usuario
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para email: {}", request.getEmail());

        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado: {}", request.getEmail());
                    return new IllegalArgumentException("Credenciales inválidas");
                });

        // Verificar que el usuario esté activo
        if (!usuario.getActivo()) {
            log.error("Usuario inactivo: {}", request.getEmail());
            throw new IllegalArgumentException("Usuario inactivo");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            log.error("Contraseña incorrecta para: {}", request.getEmail());
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Actualizar último acceso
        usuarioService.actualizarUltimoAcceso(usuario.getId());

        // Generar token JWT
        String token = jwtService.generateToken(
                usuario.getEmail(),
                usuario.getId(),
                usuario.getRol().name()
        );

        log.info("Login exitoso para usuario: {} ({})", usuario.getEmail(), usuario.getRol());

        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .userId(usuario.getId())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol())
                .build();
    }

    /**
     * Valida un token JWT
     */
    public boolean validateToken(String token, String email) {
        return jwtService.isTokenValid(token, email);
    }

    /**
     * Extrae el email del token
     */
    public String extractEmailFromToken(String token) {
        return jwtService.extractUsername(token);
    }
}
