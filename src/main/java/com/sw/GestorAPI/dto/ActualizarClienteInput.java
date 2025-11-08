package com.sw.GestorAPI.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ActualizarClienteInput {
    private String nombre;
    
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    private String telefono;
    private String direccion;
    private Boolean activo;
}
