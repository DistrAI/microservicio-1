package com.sw.GestorAPI.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AjustarInventarioInput {
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;
    
    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;
    
    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;
}
