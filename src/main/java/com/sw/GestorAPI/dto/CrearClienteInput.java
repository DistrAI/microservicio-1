package com.sw.GestorAPI.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrearClienteInput {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    private String telefono;
    
    // Campos de ubicación del cliente
    private String direccion;
    private Double latitudCliente;
    private Double longitudCliente;
    private String referenciaDireccion;
}
