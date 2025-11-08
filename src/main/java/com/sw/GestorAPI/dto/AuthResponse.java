package com.sw.GestorAPI.dto;

import com.sw.GestorAPI.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de autenticación
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String tipo;
    private Long userId;
    private String email;
    private String nombreCompleto;
    private Rol rol;
    
    @Builder.Default
    private String tokenType = "Bearer";
}
