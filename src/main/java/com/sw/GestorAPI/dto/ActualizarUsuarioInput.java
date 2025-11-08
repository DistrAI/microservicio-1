package com.sw.GestorAPI.dto;

import com.sw.GestorAPI.enums.Rol;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar un usuario existente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarUsuarioInput {

    private String nombreCompleto;

    @Email(message = "El email debe ser válido")
    private String email;

    private String password;

    private Rol rol;

    private String telefono;

    private Boolean activo;
}
