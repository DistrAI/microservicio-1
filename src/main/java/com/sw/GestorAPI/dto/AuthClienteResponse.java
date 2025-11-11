package com.sw.GestorAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de autenticación de clientes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthClienteResponse {
    /**
     * Token JWT para autenticación del cliente
     */
    private String token;
    
    /**
     * Tipo de token (Bearer)
     */
    private String tipo;
    
    /**
     * ID del cliente autenticado
     */
    private Long clienteId;
    
    /**
     * Email del cliente
     */
    private String email;
    
    /**
     * Nombre del cliente
     */
    private String nombre;
}
